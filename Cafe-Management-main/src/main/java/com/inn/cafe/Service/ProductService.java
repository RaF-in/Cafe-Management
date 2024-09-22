package com.inn.cafe.Service;

import com.inn.cafe.Models.Product;
import com.inn.cafe.Repository.ProductRepo;
import com.inn.cafe.ReqestDTO.ProductSaveRequestDTO;
import com.inn.cafe.ResponseDTO.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    ProductRepo productRepo;

    public GenericResponse<List<Product>> getAllProducts() {
//        if (userService.isAdmin()) {
//            return GenericResponse.success(productRepo.findAll());
//        } else {
//            return GenericResponse.success(productRepo.findByQuantityGreaterThan(0L));
//        }
        return GenericResponse.success(productRepo.findAll());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public GenericResponse<String> saveUpdateProducts(ProductSaveRequestDTO productSaveRequestDTO) {
        if (productSaveRequestDTO.isDelete()) {
            productRepo.deleteAllById(productSaveRequestDTO.getProductList()
                    .stream().map(prod -> prod.getId()).collect(Collectors.toList()));
            return GenericResponse.success("Products Deleted Successfully");
        } else {
            productRepo.saveAll(productSaveRequestDTO.getProductList());
            return GenericResponse.success("Products Updated Successfully");
        }
    }
}
