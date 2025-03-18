package vn.restaurant.management.utils;

import vn.restaurant.management.constant.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    /**
     * Trả về ngày giờ hiện tại theo định dạng yyyy/MM/dd HH:mm:ss dưới dạng Date
     * @return Ngày giờ hiện tại dưới dạng Date
     */
    public static Date getCurrentDateTime() {
        SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        String formattedDateTime = DATE_FORMATTER.format(new Date()); // Định dạng thời gian hiện tại
        try {
            return DATE_FORMATTER.parse(formattedDateTime); // Chuyển đổi chuỗi thành Date
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date", e); // Xử lý lỗi nếu có
        }
    }
}
