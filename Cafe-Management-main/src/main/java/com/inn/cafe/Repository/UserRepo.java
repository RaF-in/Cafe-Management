package com.inn.cafe.Repository;

import com.inn.cafe.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> { ;

    User findByEmail(String email);

    List<User> findByRoles(String user);

    @Query(value = "SELECT * FROM User u WHERE u.email in :collect", nativeQuery = true)
    List<User> findByEmail(List<String> collect);
}
