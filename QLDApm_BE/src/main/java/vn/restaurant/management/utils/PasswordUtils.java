package vn.restaurant.management.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
    /**
     * Thực hiện việc mã hóa một mật khẩu thông qua thuật toán BCrypt
     * nhận vào một chuỗi sau đó trã về một chuỗi đã mã hóa
     * @param plainPassword chuỗi muốn mã hóa
     * @return encodedPassword trả về một chuổi được mã hóa
     */
    public static String generatePassword(String plainPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(plainPassword);
    }

    /**
     * So sánh một chuỗi đã mã hóa và một chuỗi được chưa mã hóa
     * @param plainPassword mật khẩu đuợc nhập
     * @param encodedPassword mật khẩu dã được mã hóa
     * @return
     */
    public static Boolean checkPassword(String plainPassword, String encodedPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(plainPassword, encodedPassword);
    }

    public static void main(String[] args) {
        System.out.println(generatePassword("Test1234@"));
    }
}
