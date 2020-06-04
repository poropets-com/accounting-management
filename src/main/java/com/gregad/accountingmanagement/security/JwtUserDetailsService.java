package com.gregad.accountingmanagement.security;

import com.gregad.accountingmanagement.model.Status;
import lombok.extern.slf4j.Slf4j;
import com.gregad.accountingmanagement.model.UserEntity;
import com.gregad.accountingmanagement.security.jwt.JwtUser;
import com.gregad.accountingmanagement.security.jwt.JwtUserFactory;
import com.gregad.accountingmanagement.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {
    private final IUserService userService;

    @Autowired
    public JwtUserDetailsService(IUserService accountingService){
        this.userService =accountingService;
    }
   
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity= userService.getUserEntityByEmail(email);
        if (userEntity==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Username or Password is wrong");
        }
        if (!userEntity.getStatus().equals(Status.ACTIVE)){
            log.error("IN loadUserByUsername - Username: {} blocked to use service",userEntity.getEmail());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Username: "+userEntity.getEmail()+" blocked to use service");
        }
        JwtUser jwtUser= JwtUserFactory.create(userEntity);
        log.info("IN loadUserByUsername - user with email: {} successfully loaded",email);
        return jwtUser;
    }
}
