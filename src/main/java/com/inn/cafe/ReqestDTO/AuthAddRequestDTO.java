package com.inn.cafe.ReqestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthAddRequestDTO {
    String name;
    List<Long> permissionsId;
    @JsonProperty("isRole")
    boolean isRole;
}
