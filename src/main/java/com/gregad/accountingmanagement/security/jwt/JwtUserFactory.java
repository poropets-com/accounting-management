package com.gregad.accountingmanagement.security.jwt;

import com.gregad.accountingmanagement.model.RoleEntity;
import com.gregad.accountingmanagement.model.Status;
import com.gregad.accountingmanagement.model.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class JwtUserFactory {
    public JwtUserFactory (){
    }

    public static JwtUser create(UserEntity userEntity){
        return new JwtUser(
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getStatus().equals(Status.ACTIVE),
                mapToGrantedAuthorities(new ArrayList<>(userEntity.getRoles())));
    }
    public static List<GrantedAuthority> mapToGrantedAuthorities(List<RoleEntity>roles){
        return roles.stream().map(r->new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }
}
