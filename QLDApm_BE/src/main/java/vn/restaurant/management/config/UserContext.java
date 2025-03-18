package vn.restaurant.management.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    /**
     * Lấy thông tin userId từ JWT token thông qua Spring Security Context.
     *
     * Phương thức này truy cập `SecurityContextHolder` để lấy thông tin xác thực
     * của người dùng hiện tại. Nếu có thông tin xác thực và người dùng đã được
     * xác thực (`isAuthenticated()` trả về true), phương thức sẽ trả về `userId`
     * (lấy từ `authentication.getPrincipal()`).
     *
     * @return userId của người dùng hiện tại dưới dạng chuỗi (String), hoặc null
     * nếu không tìm thấy thông tin xác thực hoặc người dùng chưa đăng nhập.
     */
    public String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                return jwt.getClaimAsString("Userid");
            }
        }
        return null;
    }
}