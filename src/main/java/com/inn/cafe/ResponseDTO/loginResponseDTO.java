package com.inn.cafe.ResponseDTO;

import com.inn.cafe.Models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class loginResponseDTO {
    private String email;
    private String jwtToken;
    private Date expiresIn;
    private User userData;
}
