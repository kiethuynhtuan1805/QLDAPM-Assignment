package vn.restaurant.management.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataResponse<T> {
    private Boolean status;
    private String message = "";
    private T data;

    public static <T> DataResponse<T> successResponse(T data,String message) {
        return DataResponse.<T>builder()
                .status(true)
                .message(message)
                .data(data)
                .build();
    }
    public static <T> DataResponse<T> successResponse(String message) {
        return DataResponse.<T>builder()
                .status(true)
                .message(message)
                .build();
    }

    public static <T> DataResponse<T> failResponse(String message) {
        return DataResponse.<T>builder()
                .status(false)
                .message(message)
                .build();
    }

    public static <T> DataResponse<T> failResponse(T data, String message) {
        return DataResponse.<T>builder()
                .status(false)
                .message(message)
                .data(data)
                .build();
    }
}
