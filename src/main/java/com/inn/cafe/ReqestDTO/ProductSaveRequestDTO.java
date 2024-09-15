package com.inn.cafe.ReqestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inn.cafe.Models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSaveRequestDTO {
    private List<Product> productList;
    @JsonProperty("isDelete")
    private boolean isDelete;
}
