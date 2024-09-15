package com.inn.cafe.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inn.cafe.Models.User;
import com.inn.cafe.Repository.UserRepo;
import com.inn.cafe.ReqestDTO.loginRequestDTO;
import com.inn.cafe.ResponseDTO.GenericResponse;
import com.inn.cafe.ResponseDTO.loginResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserService userService;

    @Autowired
    UserLoginAndSignupService userLoginAndSignupService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
        String email = user.getAttribute("email").toString();
        String userName = user.getName();
        User userFromDb = userRepo.findByEmail(email);
        boolean resp = true;
        if (userFromDb != null) {
            GenericResponse<loginResponseDTO>respObj = redirectToLogin(userFromDb);
            if ("Invalid Credentials".equals(respObj.getMessage())) {
                resp = false;
            } else {
                loginOrSignupSuccess(response, respObj);
            }
        } else {
            GenericResponse<String> successResponseObj = redirectToSignUp(email, userName);
            if ("User Already exists".equals(successResponseObj.getMessage())) {
                resp = false;
            } else {
                loginOrSignupSuccess(response, successResponseObj);
            }
        }

        if (!resp) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to handle post request");
        }
    }

    private <T> void loginOrSignupSuccess(HttpServletResponse response, GenericResponse<T> successResponseObj) throws IOException {
        Map<String, GenericResponse<T>> successResponse = new HashMap<>();
        successResponse.put("response", successResponseObj);
        // Set response status and content type
        response.setStatus(HttpServletResponse.SC_OK);  // HTTP 200 OK
        response.setContentType("application/json");

        // Write the JSON response body
        response.getWriter().write(objectMapper.writeValueAsString(successResponse));
        response.getWriter().flush();
    }


    public GenericResponse<String> redirectToSignUp(String email, String userName) {
        // Create a request body (this is the body you want to send to the target URL)
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("username", userName);
        requestBody.put("password", "password");
        requestBody.put("contactNumber", "1111");
        requestBody.put("roles", "ROLE_USER");


        return userLoginAndSignupService.signup(requestBody);


    }

    public GenericResponse<loginResponseDTO> redirectToLogin(User user) {
        // Create a request body (this is the body you want to send to the target URL)
        loginRequestDTO userReq = new loginRequestDTO();
        userReq.setUsername(user.getUsername());
        userReq.setEmail(user.getEmail());
        userReq.setPassword("password");
        return  userLoginAndSignupService.userSuccessLogin(userReq);
    }
}
