package com.gregad.accountingmanagement.dto.responseDto;


import java.util.HashSet;

//usage responses to login, User information, edit profile, remove user endpoints
public class UserInformationResponseDto {
    private String avatar;
    private String name;
    private String email;
    private String phone;
    private boolean block;
    private HashSet<String> roles;

    public UserInformationResponseDto(String avatar, String name, String email, String phone, boolean block, HashSet<String> roles) {
        this.avatar = avatar;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.block = block;
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

    public String getPhone() {
        return phone;
    }

    public boolean isBlock() {
        return block;
    }

    public HashSet<String> getRoles() {
        return roles;
    }
}
