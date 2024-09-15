package com.inn.cafe.Repository;

import com.inn.cafe.Models.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepo extends JpaRepository<Bill, Long> {

}
