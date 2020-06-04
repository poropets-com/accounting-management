package com.gregad.accountingmanagement.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileRequestDto {
    private String avatar;
    private String name;
    private String phone;

    public EditProfileRequestDto(String avatar, String name, String phone) {
        this.avatar = avatar;
        this.name = name;
        this.phone = phone;
    }

  
}
