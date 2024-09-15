package com.inn.cafe.Controller;

import com.inn.cafe.Models.Product;
import com.inn.cafe.ReqestDTO.ProductSaveRequestDTO;
import com.inn.cafe.ResponseDTO.GenericResponse;
import com.inn.cafe.Service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/getAllProducts")
    private GenericResponse<List<Product>> getAllProducts() {
        try {
            return productService.getAllProducts();
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return GenericResponse.error(ex.getMessage());
        }
    }

    @PostMapping("/saveUpdateProduct")
    private GenericResponse<String> saveUpdateProducts(@RequestBody ProductSaveRequestDTO productSaveRequestDTO) {
        try {
            return productService.saveUpdateProducts(productSaveRequestDTO);
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return GenericResponse.error(ex.getMessage());
        }
    }
}
