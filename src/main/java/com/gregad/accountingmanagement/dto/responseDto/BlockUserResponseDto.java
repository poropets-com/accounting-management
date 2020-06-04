package com.gregad.accountingmanagement.dto.responseDto;

public class BlockUserResponseDto {
    private String login;
    private boolean block;

    public BlockUserResponseDto(String login, boolean block) {
        this.login = login;
        this.block = block;
    }

    public String getLogin() {
        return login;
    }

    public boolean isBlock() {
        return block;
    }
}
