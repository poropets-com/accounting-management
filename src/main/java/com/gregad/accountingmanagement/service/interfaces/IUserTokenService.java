package com.gregad.accountingmanagement.service.interfaces;

public interface IUserTokenService {
    void addToken(String email,String token);
    void deleteToken(String email);
    boolean tokenValidation(String email,String jwtToken);
}
