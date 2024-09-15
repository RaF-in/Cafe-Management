package com.inn.cafe.Repository;

import com.inn.cafe.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {

    List<Product> findByQuantityGreaterThan(Long number);
}
