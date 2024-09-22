package com.inn.cafe.ReqestDTO;

import com.inn.cafe.Models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUsersRequestDTO {
    private List<User> userList;
}
