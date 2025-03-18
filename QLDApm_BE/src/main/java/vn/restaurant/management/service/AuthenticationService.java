package vn.restaurant.management.service;

import org.springframework.stereotype.Service;
import vn.restaurant.management.dto.request.AuthenticationRequest;
import vn.restaurant.management.dto.request.IntrospecRequest;
import vn.restaurant.management.dto.request.LogoutRequest;
import vn.restaurant.management.dto.request.RefreshTokenRequest;
import vn.restaurant.management.dto.response.AuthenticationResponse;
import vn.restaurant.management.dto.response.IntrospecResponse;
import vn.restaurant.management.entity.User;
import vn.restaurant.management.exception.authentication.AuthenticationNotFoundException;

public interface AuthenticationService {
    boolean authenticate(AuthenticationRequest request) throws AuthenticationNotFoundException;

    void logout(LogoutRequest request);

    AuthenticationResponse login(AuthenticationRequest request) throws AuthenticationNotFoundException;

    AuthenticationResponse refreshToken(RefreshTokenRequest request) throws AuthenticationNotFoundException;

    boolean introspect(IntrospecRequest request) throws AuthenticationNotFoundException;
}
