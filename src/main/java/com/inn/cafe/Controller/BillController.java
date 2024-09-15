package com.inn.cafe.Controller;

import com.inn.cafe.Models.Bill;
import com.inn.cafe.ReqestDTO.BillUpdateRequestDTO;
import com.inn.cafe.ResponseDTO.GenericResponse;
import com.inn.cafe.Service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/bill")
@Slf4j
public class BillController {
    @Autowired
    BillService billService;
    @PostMapping("/generateBill")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    private GenericResponse<String> generateBill(@RequestBody Bill request) {
        try {
            return billService.generateBill(request);
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return GenericResponse.error(ex.getMessage());
        }
    }

    @PostMapping("/updateBill")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    private GenericResponse<Bill> updateBill(@RequestBody BillUpdateRequestDTO request) {
        try {
            return billService.updateBill(request);
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return GenericResponse.error(ex.getMessage());
        }
    }

    @PostMapping("/downloadBill")
    private GenericResponse<String> downloadBill(@RequestBody Map<String, Long> request) {
        if (!validateMap(request)) {
            return GenericResponse.error("Please provide Bill id");
        }
        try {
            return billService.downloadBill(request.get("id"));
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return GenericResponse.error(ex.getMessage());
        }
    }

    private boolean validateMap(Map<String, Long> request) {
        return request.containsKey("id");
    }
}
