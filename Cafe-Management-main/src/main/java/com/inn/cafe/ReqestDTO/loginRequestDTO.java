package com.inn.cafe.ReqestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class loginRequestDTO {
    String password;
    String username;
    String email;
}
