package com.inn.cafe.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inn.cafe.Models.User;
import com.inn.cafe.Repository.UserRepo;
import com.inn.cafe.ReqestDTO.loginRequestDTO;
import com.inn.cafe.ResponseDTO.GenericResponse;
import com.inn.cafe.ResponseDTO.loginResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    UserLoginAndSignupService userLoginAndSignupService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        GenericResponse<loginResponseDTO> loginResponse = userLoginAndSignupService.handleSocialLogin((OAuth2User) authentication.getPrincipal());
        loginResponseDTO resp = loginResponse.getData();
        // Extract user information
        String email = resp.getEmail();
        String jwtToken = resp.getJwtToken();
        Date expiresIn = resp.getExpiresIn();
        // Set each piece of user info in a cookie
        setCookie(response, "email", email, 24 * 60 * 60);
        setCookie(response, "jwtToken", jwtToken, 24 * 60 * 60);

        // Format date as a string (ISO 8601 or any other preferred format)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String formattedDate = dateFormat.format(expiresIn);

        // URL encode the formatted date to make it a valid cookie value
        String encodedDate = URLEncoder.encode(formattedDate, StandardCharsets.UTF_8);
        setCookie(response, "expiresIn", encodedDate, 24 * 60 * 60);  // Optional picture URL

        response.sendRedirect("http://localhost:4200/handleSocialLogin");
    }

    private void setCookie(HttpServletResponse response, String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);// Use secure flag in production
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
