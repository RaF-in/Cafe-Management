package com.inn.cafe.Service;

import com.inn.cafe.Models.User;
import com.inn.cafe.Repository.UserRepo;
import com.inn.cafe.ReqestDTO.loginRequestDTO;
import com.inn.cafe.ResponseDTO.GenericResponse;
import com.inn.cafe.ResponseDTO.loginResponseDTO;
import com.inn.cafe.jwt.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserLoginAndSignupService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtHelper jwtHelper;

    @Autowired
    private AuthenticationManager manager;
    public GenericResponse<loginResponseDTO> signup(Map<String, String> request) {
        User user = userRepo.findByEmail(request.get("email"));
        if (user != null) {
            return GenericResponse.badRequest("User Already exists");
        } else {
            return createNewUserForSignup(request);
        }
    }

    public GenericResponse<loginResponseDTO> createNewUserForSignup(Map<String, String> request) {
        User user = createNewUser(request);
        userRepo.save(user);
        loginRequestDTO userReq = new loginRequestDTO();
        userReq.setUsername(user.getUsername());
        userReq.setEmail(user.getEmail());
        userReq.setPassword("password");
        return userSuccessLogin(userReq, user);
    }

    private User createNewUser(Map<String, String> requestMap) {
        User user = User.builder()
                .username(requestMap.get("username"))
                .email(requestMap.get("email"))
                .contactNumber(requestMap.get("contactNumber"))
                .roles(requestMap.get("roles"))
                .status(requestMap.get("status"))
                .password(passwordEncoder.encode(requestMap.get("password")))
                .build();
        return user;
    }

    public GenericResponse<loginResponseDTO> login(loginRequestDTO userReq) {
        if (doAuthenticate(userReq.getEmail(), userReq.getPassword())) {
            return userSuccessLogin(userReq, null);
        } else {
            return GenericResponse.error("Invalid Credentials");
        }
    }

    public GenericResponse<loginResponseDTO> userSuccessLogin(loginRequestDTO userReq, User user) {
        String token = this.jwtHelper.generateToken(userReq.getEmail());
        if (user == null) {
            user = this.userRepo.findByEmail(userReq.getEmail());
        }
        loginResponseDTO response = loginResponseDTO
                .builder()
                .jwtToken(token)
                .email(userReq.getEmail())
                .expiresIn(this.jwtHelper.getExpirationDateFromToken(token))
                .userData(user)
                .build();
        return GenericResponse.success(response);
    }

    public boolean doAuthenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        try {
            return manager.authenticate(authentication).isAuthenticated();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Email or Password  !!");
        }
    }
}
