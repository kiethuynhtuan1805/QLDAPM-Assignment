package vn.restaurant.management.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

public class GeneralUtils {

    public static String generateId() {
        // Bước 1: Lấy thời gian hiện tại và định dạng theo yyyyMMddss
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddss");
        String timestamp = now.format(formatter);

        // Bước 2: Tạo chuỗi ngẫu nhiên và mã hóa Base64
        byte[] randomBytes = new byte[6]; // Tạo 6 byte ngẫu nhiên
        new Random().nextBytes(randomBytes);
        String randomBase64 = Base64.getEncoder().encodeToString(randomBytes);

        // Bước 3: Kết hợp timestamp và randomBase64 để tạo ID
        String combinedId = timestamp + randomBase64.replaceAll("=", ""); // Loại bỏ dấu '=' nếu có

        // Bước 4: Loại bỏ tất cả ký tự đặc biệt, chỉ giữ lại số và chữ
        return combinedId.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static void main(String[] args) {
        System.out.println(generateId());
    }
}
