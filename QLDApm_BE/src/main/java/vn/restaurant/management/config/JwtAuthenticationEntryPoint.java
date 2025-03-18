package vn.restaurant.management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import vn.restaurant.management.dto.response.DataResponse;


import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        // Đặt status code cho response
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Tạo đối tượng ApiResponse để phản hồi
        DataResponse<?> apiResponse = DataResponse.builder()
                .status(false)
                .message("Ban khong co quyen truy cap.")
                .build();

        // Sử dụng ObjectMapper để ghi JSON vào response
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}

