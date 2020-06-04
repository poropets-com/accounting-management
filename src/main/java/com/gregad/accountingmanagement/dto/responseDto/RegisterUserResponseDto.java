package com.gregad.accountingmanagement.dto.responseDto;



import java.util.HashSet;

public class RegisterUserResponseDto {
    private String avatar;
    private String name;
    private String email;
    private HashSet<String> roles;

    public RegisterUserResponseDto(String avatar, String name, String email, HashSet<String> roles) {
        this.avatar = avatar;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public HashSet<String> getRoles() {
        return roles;
    }
}
