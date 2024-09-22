package com.inn.cafe.ReqestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inn.cafe.Models.Bill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillUpdateRequestDTO {
    private Bill bill;
    @JsonProperty("isDelete")
    private boolean isDelete;
}
