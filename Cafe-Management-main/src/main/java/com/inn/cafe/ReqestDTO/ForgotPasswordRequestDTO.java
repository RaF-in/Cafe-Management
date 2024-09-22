package com.inn.cafe.ReqestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequestDTO {
    private String newPassword;
    private String passwordRepeat;
    private String email;
}
