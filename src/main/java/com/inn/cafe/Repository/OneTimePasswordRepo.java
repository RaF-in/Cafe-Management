package com.inn.cafe.Repository;

import com.inn.cafe.Models.OneTimePassword;
import com.inn.cafe.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OneTimePasswordRepo extends JpaRepository<OneTimePassword, Long> {
    @Query(value="Select * from one_time_password one where one.otp=:otp and one.user_id=:id", nativeQuery=true)
    public Optional<OneTimePassword> getOneTimePasswordByUserAndOtp(int otp, Long id);
    OneTimePassword getOneTimePasswordByUser(User user);
}
