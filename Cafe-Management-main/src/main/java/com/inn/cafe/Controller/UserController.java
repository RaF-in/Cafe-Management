package com.inn.cafe.Controller;

import com.inn.cafe.Models.User;
import com.inn.cafe.ReqestDTO.ForgotPasswordRequestDTO;
import com.inn.cafe.ReqestDTO.UpdateUsersRequestDTO;
import com.inn.cafe.ReqestDTO.VerifyOtpRequestDTO;
import com.inn.cafe.ReqestDTO.loginRequestDTO;
import com.inn.cafe.ResponseDTO.GenericResponse;
import com.inn.cafe.ResponseDTO.loginResponseDTO;
import com.inn.cafe.Service.UserLoginAndSignupService;
import com.inn.cafe.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserLoginAndSignupService userLoginAndSignupService;

    @PostMapping("/signup")
    public GenericResponse<loginResponseDTO> signup(@RequestBody(required = true) Map<String, String> request) {
        try {
            if (isValid(request)) {
                return userLoginAndSignupService.signup(request);
            } else {
                return GenericResponse.badRequest("Please provide all necessary fields for signup");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return GenericResponse.error(e.getMessage());
        }
    }

    private boolean isValid(Map<String, String> request) {
        return request.containsKey("username") && request.containsKey("password") && request.containsKey("email");
    }

    @PostMapping("/login")
    public GenericResponse<loginResponseDTO> login(@RequestBody loginRequestDTO userReq) {

        try {
            return userLoginAndSignupService.login(userReq);
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return GenericResponse.error(ex.getMessage());
        }
    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GenericResponse<List<User>> getAllUsers() {
        try {
            return userService.getAllUsers();
        } catch(Exception ex) {
            log.error(ex.getMessage(),ex);
            return GenericResponse.error(ex.getMessage());
        }
    }

    @PostMapping("/updateUsers")
    public GenericResponse<List<String>> updateUsers(@RequestBody UpdateUsersRequestDTO request) {
        try {
            return userService.updateAllUsers(request.getUserList());
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return GenericResponse.error(ex.getMessage());
        }
    }

    @PostMapping("/forgotPassword")
    public GenericResponse<String> forgotPasword(@RequestBody Map<String, String> forgotPasswordMap) {
        try {
            return userService.forgotPassword(forgotPasswordMap.get("email"));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return GenericResponse.error(ex.getMessage());
        }
    }

    @PostMapping("/verifyOtp")
    public GenericResponse<String> verifyOtp(@RequestBody VerifyOtpRequestDTO verifyOtpRequestDTO) {
        try {
            return userService.verifyOtp(verifyOtpRequestDTO);
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return GenericResponse.error(ex.getMessage());
        }
    }

    @PostMapping("/changePassword")
    public GenericResponse<String> changePassword(@RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        try {
            return userService.changePassword(forgotPasswordRequestDTO);
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return GenericResponse.error(ex.getMessage());
        }
    }
}
