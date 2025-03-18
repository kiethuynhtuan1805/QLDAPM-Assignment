package vn.restaurant.management.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
public class PrintUtils {
    public static void print(Object obj) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ip = "Unknown";

        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            if (request != null) {
                ip = request.getRemoteAddr();
            }
        }

        String className = obj.getClass().getName();
        String jsonData = "";

        try {
            jsonData = new Gson().toJson(obj);
        } catch (Exception e) {
            log.warn("Failed to convert object to JSON, using default log format instead. Error: {}", e.getMessage());
            jsonData = "Unable to serialize object"; // Hoặc có thể để trống
        }

        log.info("ip={}, \n class={}, \n data={}", ip, className, jsonData);
    }

}
