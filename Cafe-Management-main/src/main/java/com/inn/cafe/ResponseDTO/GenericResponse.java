package com.inn.cafe.ResponseDTO;

import com.inn.cafe.util.Enums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GenericResponse<T> {
    private String message;
    private Enums.MESSAGE_TYPE message_type;
    private T data;

    public static <T> GenericResponse<T> empty() {
        return success(null);
    }

    public static <T> GenericResponse<T> success(T data) {
        return GenericResponse.<T>builder()
                .message_type(Enums.MESSAGE_TYPE.SUCCESS)
                .data(data)
                .build();
    }

    public static <T> GenericResponse<T> error(String message) {
        return GenericResponse.<T>builder()
                .message(message)
                .message_type(Enums.MESSAGE_TYPE.ERROR)
                .build();
    }

    public static <T> GenericResponse<T> badRequest(String message) {
        return GenericResponse.<T>builder()
                .message(message)
                .message_type(Enums.MESSAGE_TYPE.BAD_REQUEST)
                .build();
    }

    public static <T> GenericResponse<T> unauthorized(String message) {
        return GenericResponse.<T>builder()
                .message(message)
                .message_type(Enums.MESSAGE_TYPE.UNAUTHORIZED)
                .build();
    }
}