package vn.restaurant.management.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.AuthenticationRequest;
import vn.restaurant.management.dto.request.IntrospecRequest;
import vn.restaurant.management.dto.request.LogoutRequest;
import vn.restaurant.management.dto.request.RefreshTokenRequest;
import vn.restaurant.management.dto.response.AuthenticationResponse;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.dto.response.IntrospecResponse;
import vn.restaurant.management.exception.authentication.AuthenticationNotFoundException;
import vn.restaurant.management.service.AuthenticationService;

import java.util.List;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class AuthenticationRestController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public DataResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.login(request);
            return DataResponse.successResponse(authenticationResponse, "Đăng nhập thành công");
        } catch (AuthenticationNotFoundException ex) {
            log.error("Đăng nhập thất bại, lý do={}", ex.getMessage());
            return DataResponse.failResponse("Đăng nhập thất bại: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Lỗi không mong muốn trong quá trình đăng nhập, lý do={}", ex.getMessage());
            return DataResponse.failResponse("Đã xảy ra lỗi không mong muốn trong quá trình đăng nhập.");
        }
    }

    @PostMapping("/introspect")
    public DataResponse<IntrospecResponse> introspect(@RequestBody IntrospecRequest request) {
        try {
            boolean isValid = authenticationService.introspect(request);
            IntrospecResponse introspecResponse = IntrospecResponse.builder()
                    .valid(isValid)
                    .build();
            String message = isValid ? "Kiểm tra hợp lệ token thành công" : "Kiểm tra hợp lệ token thất bại";
            return DataResponse.successResponse(introspecResponse, message);
        } catch (AuthenticationNotFoundException ex) {
            log.error("Kiểm tra hợp lệ token thất bại, lý do={}", ex.getMessage());
            IntrospecResponse introspecResponse = IntrospecResponse.builder()
                    .valid(false)
                    .build();
            return DataResponse.failResponse(introspecResponse, "Kiểm tra hợp lệ token thất bại: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Lỗi không mong muốn trong quá trình kiểm tra hợp lệ token, lý do={}", ex.getMessage());
            return DataResponse.failResponse(null, "Đã xảy ra lỗi không mong muốn trong quá trình kiểm tra hợp lệ token.");
        }
    }

    @PostMapping("/refresh")
    public DataResponse<AuthenticationResponse> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.refreshToken(request);
            return DataResponse.successResponse(authenticationResponse, "Làm mới token thành công");
        } catch (AuthenticationNotFoundException ex) {
            log.error("Làm mới token thất bại, lý do={}", ex.getMessage());
            return DataResponse.failResponse(null, "Làm mới token thất bại: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Lỗi không mong muốn trong quá trình làm mới token, lý do={}", ex.getMessage());
            return DataResponse.failResponse(null, "Đã xảy ra lỗi không mong muốn trong quá trình làm mới token.");
        }
    }

    @PostMapping("/logout")
    public DataResponse<Void> logout(@RequestBody LogoutRequest request) {
        try {
            authenticationService.logout(request);
            return DataResponse.successResponse(null, "Đăng xuất thành công");
        } catch (Exception ex) {
            log.error("Đăng xuất thất bại, lý do={}", ex.getMessage());
            return DataResponse.failResponse(null, "Đã xảy ra lỗi không mong muốn trong quá trình đăng xuất.");
        }
    }
}
