package com.inn.cafe.Service;

import com.inn.cafe.DTO.UserWrapDTO;
import com.inn.cafe.Models.OneTimePassword;
import com.inn.cafe.Models.User;
import com.inn.cafe.Repository.OneTimePasswordRepo;
import com.inn.cafe.Repository.UserRepo;
import com.inn.cafe.ReqestDTO.ForgotPasswordRequestDTO;
import com.inn.cafe.ReqestDTO.VerifyOtpRequestDTO;
import com.inn.cafe.ResponseDTO.GenericResponse;
import com.inn.cafe.jwt.JwtHelper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    EmailNotificationService emailNotificationService;

    @Autowired
    OneTimePasswordRepo oneTimePasswordRepo;

    @Autowired
    JwtHelper jwtHelper;

    public Claims claims;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserWrapDTO(user);
    }

    public GenericResponse<List<User>> getAllUsers() {
        if (this.jwtHelper.isAdmin()) {
            List<User> userList = userRepo.findByRoles("ROLE_USER");
            return GenericResponse.success(userList);
        } else {
            return GenericResponse.unauthorized("Unauthorized access");
        }
    }


    public GenericResponse<List<String>> updateAllUsers(List<User> request) throws Exception {
        List<String> response = new ArrayList<>();
        List<User> users = userRepo.findByEmail(request.stream().map(User::getEmail)
                .collect(Collectors.toList()));
        List<User> usersToUpdate = new ArrayList<>();
        List<String> approvedUsers = new ArrayList<>();

        for (User user: request) {
            User userFromDb = users.stream().filter(usr -> usr.getEmail().equals(user.getEmail())).findAny().orElse(null);
            if (userFromDb == null) {
                response.add("User not found " + user.getUsername());
            } else if (this.jwtHelper.isAdmin() || (userFromDb.getRoles().equals(user.getRoles()) && user.getEmail()
                    .equals(this.jwtHelper.getCurrentUser().getEmail()))) {
                if (!userFromDb.getStatus().equals(user.getStatus())) {
                    approvedUsers.add(user.getEmail());
                }
                BeanUtils.copyProperties(user, userFromDb, "id","username","password");
                usersToUpdate.add(userFromDb);
            } else {
                response.add("You are not authorized to update user " + user.getUsername());
            }
         }
        try {
            userRepo.saveAll(usersToUpdate);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        if (approvedUsers.size() > 0) {
            List<User> adminList = userRepo.findByRoles("ROLE_ADMIN");
            adminList.removeIf(usr -> usr.getEmail().equals(this.jwtHelper.getCurrentUser().getEmail()));
            List<String> ccAdminList = adminList.stream().map(User::getEmail).collect(Collectors.toList());
            emailNotificationService.sendSimpleEmail(this.jwtHelper.getCurrentUser().getEmail(), "Users Activated!!", "Following Users are approved by " + this.jwtHelper.getCurrentUser().getUsername(), ccAdminList);
        }
        return GenericResponse.success(response);
    }

    public void setClaims(Claims claims) {
        this.claims = claims;
    }

    public GenericResponse<String> forgotPassword(String email) throws Exception {
        User userFromDB = userRepo.findByEmail(email);
        if (userFromDB == null) {
            throw new UsernameNotFoundException("User not found.");
        } else {
            deleteAnyExistingOtpEntry(userFromDB);
            int otp = getNewOtp();
            OneTimePassword oneTimePassword = OneTimePassword.builder()
                    .otp(otp)
                    .user(userFromDB)
                    .expirationTime(new Date(System.currentTimeMillis() + 2 * 60 * 1000))
                    .build();
            oneTimePasswordRepo.save(oneTimePassword);
            String body = "Your otp for forget password is " + otp;
            emailNotificationService.sendSimpleEmail(userFromDB.getEmail(), "OTP for change Password", body, null);
            return GenericResponse.success("OTP sent successfully to your email address");
        }
    }

    private void deleteAnyExistingOtpEntry(User user) {
        OneTimePassword oneTimePassword = oneTimePasswordRepo.getOneTimePasswordByUser(user);
        if (oneTimePassword != null) {
            oneTimePasswordRepo.delete(oneTimePassword);
        }

    }

    private int getNewOtp() {
        Random random = new Random();
        return random.nextInt(1000000, 9999999);
    }

    public GenericResponse<String> verifyOtp(VerifyOtpRequestDTO verifyOtpRequestDTO) {
        User user = userRepo.findByEmail(verifyOtpRequestDTO.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Optional<OneTimePassword> oneTimePassword = Optional.ofNullable(oneTimePasswordRepo.getOneTimePasswordByUserAndOtp(verifyOtpRequestDTO.getOtp(), user.getId())
                .orElseThrow(() -> new RuntimeException("Please try again")));
        if (oneTimePassword.get().getExpirationTime().before(Date.from(Instant.now()))) {
            return GenericResponse.error("Otp has expired");
        }
        return GenericResponse.success("Your otp matched");
    }

    public GenericResponse<String> changePassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) throws Exception {
        User user = userRepo.findByEmail(forgotPasswordRequestDTO.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!this.jwtHelper.isCurrentLoggedInUser(user.getEmail())) {
            return GenericResponse.unauthorized("You are not authorized to update password");
        }
        if (!forgotPasswordRequestDTO.getNewPassword().equals(forgotPasswordRequestDTO.getPasswordRepeat())) {
            return GenericResponse.error("Password does not match");
        }
        user.setPassword(passwordEncoder.encode(forgotPasswordRequestDTO.getNewPassword()));
        userRepo.save(user);
        return GenericResponse.success("Password updated successfully");
    }
}
