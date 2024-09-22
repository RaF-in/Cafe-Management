package com.inn.cafe.DTO;

import com.inn.cafe.Models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWrapDTO implements UserDetails {

    private String username;

    private String contactNumber;

    private String email;

    private String password;

    private List<GrantedAuthority> authorities;

    private String roles;

    public UserWrapDTO(User user) {
        this.username = user.getUsername();
        this.contactNumber = user.getContactNumber();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = Arrays.stream(user.getRoles().split(",")).map(
                SimpleGrantedAuthority::new
        ).collect(Collectors.toList());
        this.roles = user.getRoles();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
