package vn.restaurant.management.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.restaurant.management.dto.request.AuthenticationRequest;
import vn.restaurant.management.dto.request.IntrospecRequest;
import vn.restaurant.management.dto.request.LogoutRequest;
import vn.restaurant.management.dto.request.RefreshTokenRequest;
import vn.restaurant.management.dto.response.AuthenticationResponse;
import vn.restaurant.management.entity.Authentication;
import vn.restaurant.management.entity.RevokedToken;
import vn.restaurant.management.entity.Roles;
import vn.restaurant.management.entity.User;
import vn.restaurant.management.exception.authentication.AuthenticationNotFoundException;
import vn.restaurant.management.repository.AuthenticationRepository;
import vn.restaurant.management.repository.RevokedTokenRepository;
import vn.restaurant.management.repository.RolesRepository;
import vn.restaurant.management.repository.UserRepository;
import vn.restaurant.management.service.AuthenticationService;
import vn.restaurant.management.utils.GeneralUtils;
import vn.restaurant.management.utils.PasswordUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @Value("${jwt.refreshKey}")
    private String REFRESH_KEY;


    @Override
    public boolean authenticate(AuthenticationRequest request) throws AuthenticationNotFoundException {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationNotFoundException("Username không đúng"));
        return PasswordUtils.checkPassword(request.getPassword(), user.getPasswords());
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) throws AuthenticationNotFoundException {
        if (!authenticate(request)) {
            throw new AuthenticationNotFoundException("Username hoặc password không đúng");
        }
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationNotFoundException("Username không đúng"));

        String accessToken = generateToken(user);
        String refreshToken = generateRefreshToken(user);

        Authentication authentication = Authentication.builder()
                .id(GeneralUtils.generateId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        authenticationRepository.save(authentication);

        // Trả về AuthenticationResponse với thông tin người dùng
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public boolean introspect(IntrospecRequest request) throws AuthenticationNotFoundException {
        String token = request.getToken();

        if (revokedTokenRepository.existsByAccessToken(token)){
            throw new AuthenticationNotFoundException("Token đã bị thu hồi. Vui lòng đăng nhập lại.");
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            boolean verified = signedJWT.verify(verifier);
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            return verified && expiryTime != null && expiryTime.after(new Date());
        } catch (Exception e) {
            log.error("Error introspecting token", e);
            throw new AuthenticationNotFoundException("Error introspecting token :" + e);
        }
    }

    @Override
    @Transactional
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws AuthenticationNotFoundException {

        if (revokedTokenRepository.existsByAccessToken(request.getAccessToken())){
            throw new AuthenticationNotFoundException("Token đã bị thu hồi. Vui lòng đăng nhập lại.");
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(request.getRefreshToken());
            JWSVerifier verifier = new MACVerifier(REFRESH_KEY.getBytes());

            if (signedJWT.verify(verifier) &&
                    signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date())) {

                String userId = signedJWT.getJWTClaimsSet().getStringClaim("Userid");

                // Kiểm tra xem userId còn tồn tại không
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new AuthenticationNotFoundException("User ID không tồn tại hoặc đã bị xóa"));

                String newAccessToken = generateToken(user);

                RevokedToken revokedToken = RevokedToken.builder()
                        .id(GeneralUtils.generateId())
                        .accessToken(request.getAccessToken())
                        .build();
                revokedTokenRepository.save(revokedToken);

                authenticationRepository.deleteByAccessToken(request.getAccessToken());

                return AuthenticationResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(request.getRefreshToken())
                        .build();
            } else {
                throw new AuthenticationNotFoundException("Invalid or expired refresh token");
            }
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new AuthenticationNotFoundException("Error refreshing token: " + e);
        }
    }

    @Override
    public void logout(LogoutRequest request) {
        RevokedToken revokedToken = RevokedToken.builder()
                .id(GeneralUtils.generateId())
                .accessToken(request.getAccessToken())
                .build();
        revokedTokenRepository.save(revokedToken);

        // Xóa refreshToken khỏi cơ sở dữ liệu hoặc cache nếu cần
        authenticationRepository.deleteByAccessToken(request.getAccessToken());

        log.info("User logged out successfully");
    }



    private String getRoleName(User user) throws AuthenticationNotFoundException {
        Roles role = rolesRepository.findById(user.getRoleId())
                .orElseThrow(() -> new AuthenticationNotFoundException("Role not found"));
        return role.getName();
    }

    public String generateToken(User user) throws AuthenticationNotFoundException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date())
                .expirationTime(Date.from(
                        Instant.now().plus(2, ChronoUnit.DAYS)
                ))
                .claim("scope", getRoleName(user))
                .claim("Userid", user.getUserId())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Can't generate token", e);
            throw new AuthenticationNotFoundException("Error generating token for user: " + user.getUsername() + " | " + e);
        }
    }

    private String generateRefreshToken(User user) throws AuthenticationNotFoundException {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date())
                .expirationTime(Date.from(
                        Instant.now().plus(30, ChronoUnit.DAYS)
                ))
                .claim("Userid", user.getUserId())
                .build();

        JWSObject jwsObject = new JWSObject(
                new JWSHeader(JWSAlgorithm.HS512),
                new Payload(jwtClaimsSet.toJSONObject())
        );

        try {
            jwsObject.sign(new MACSigner(REFRESH_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Can't generate refresh token", e);
            throw new AuthenticationNotFoundException("Error generating refresh token for user: " + user.getUsername() + " | " + e);
        }
    }
}
