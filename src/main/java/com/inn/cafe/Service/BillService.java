package com.inn.cafe.Service;

import com.inn.cafe.Models.Bill;
import com.inn.cafe.Models.User;
import com.inn.cafe.Repository.BillRepo;
import com.inn.cafe.Repository.UserRepo;
import com.inn.cafe.ReqestDTO.BillUpdateRequestDTO;
import com.inn.cafe.ResponseDTO.GenericResponse;
import com.inn.cafe.jwt.JwtHelper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BillService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    BillRepo billRepo;
    @Autowired
    UserService userService;
    @Autowired
    JwtHelper jwtHelper;
    public GenericResponse<String> generateBill(Bill request) {
//        if (!userService.isAdmin()) {
//            return GenericResponse.unauthorized("you are not authorized to generate bill");
//        }
        User user = userRepo.findByEmail(request.getEmail());
        if (user == null) {
            return GenericResponse.error("User not found");
        }
        request.setUser(user);
        request.setBillDate(new Date(System.currentTimeMillis()));
        billRepo.save(request);
        return GenericResponse.success("Bill Generated Successfully");
    }

    public GenericResponse<Bill> updateBill(BillUpdateRequestDTO request) {
//        if (!userService.isAdmin()) {
//            return GenericResponse.unauthorized("you are not authorized to update bill");
//        }
        Optional<Bill> billFromDb = billRepo.findById(request.getBill().getId());
        if (billFromDb.isEmpty()) {
            return GenericResponse.error("Bill not found");
        }
        if (request.isDelete()) {
            billRepo.deleteById(request.getBill().getId());
            return GenericResponse.success(billFromDb.get());
        } else {
            request.getBill().setBillDate(new Date(System.currentTimeMillis()));
            billRepo.save(request.getBill());
            return GenericResponse.success(request.getBill());
        }
    }

    public GenericResponse<String> downloadBill(Long id) throws FileNotFoundException, JRException {
        String path = "C:\\Users\\shafneaz\\Downloads";
        Optional<Bill> billFromDb = Optional.ofNullable(billRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found")));
        if (!jwtHelper.isAdmin() && !jwtHelper.getCurrentUser().getEmail().equals(billFromDb.get().getEmail())) {
            return GenericResponse.unauthorized("You are not authorized to download report");
        }
        File file = ResourceUtils.getFile("classpath:static/Bill.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(billFromDb.get().getProductList());
        Map<String, Object> params = new HashMap<>();
        params.put("username", billFromDb.get().getUsername());
        params.put("Date", billFromDb.get().getBillDate());
        params.put("billInfoparam", dataSource);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
        String reportName = billFromDb.get().getUsername() + System.currentTimeMillis();
        JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\" + reportName + ".pdf");
        return GenericResponse.success("Report Printed in " + path);
    }
}
