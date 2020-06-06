package com.gregad.accountingmanagement.rest;

import com.gregad.accountingmanagement.dto.requestDto.EditProfileRequestDto;
import com.gregad.accountingmanagement.dto.requestDto.RegisterUserRequestDto;
import com.gregad.accountingmanagement.dto.responseDto.RegisterUserResponseDto;
import com.gregad.accountingmanagement.dto.responseDto.UserInformationResponseDto;
import com.gregad.accountingmanagement.security.jwt.JwtTokenProvider;
import com.gregad.accountingmanagement.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.stream.Collectors;

import static com.gregad.accountingmanagement.api.ApiConstants.*;

@RestController
public class UserController {

    private static final String HEADER ="Authorization" ;
    private static final String BEARER = "Bearer ";
    
    @Autowired
    IUserService userService;
    
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @PostMapping(value = PREFIX)
    RegisterUserResponseDto registerNewUser(@RequestBody RegisterUserRequestDto user){
        return userService.addNewUser(user);
    }
    @PostMapping(value = PREFIX+LOGIN)
    UserInformationResponseDto login(@RequestHeader(name=HEADER) String token,
                                     HttpServletResponse response){
        try {
            String parsedToken= new String(Base64.getDecoder().decode(token.substring(6,token.length()).getBytes()));
            String[] emailPassword= getEmailFromToken(parsedToken);
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailPassword[0],emailPassword[1]));
            UserInformationResponseDto responseUser= userService.login(emailPassword[0]);

            String responseToken=jwtTokenProvider.createToken(responseUser.getEmail(),
                    responseUser.getRoles().stream().collect(Collectors.toList()));
            response.setHeader(HEADER,BEARER+responseToken);
            return responseUser;
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
    
    @GetMapping(value = PREFIX+"/{email}"+INFO)
    UserInformationResponseDto getUserInfo(@PathVariable(name = "email") String email){
        return userService.getUser(email);
    }
    
    @PutMapping(value = PREFIX+"/{email}")
    UserInformationResponseDto editUser(@RequestHeader(name=HEADER) String token,
                                        @RequestBody EditProfileRequestDto requestDto,
                                        @PathVariable String email){
        return userService.editUserData(email,token,requestDto);
    }
    
    @DeleteMapping(value = PREFIX+"/{email}")
    UserInformationResponseDto deleteUser(@RequestHeader(name=HEADER) String token,
                                          @PathVariable String email){
        return userService.removeUser(email,token);
    }
    
    @GetMapping(value = PREFIX+"/{token}"+VALIDATION)
    void validateToken(@PathVariable String token,
                             HttpServletResponse response){
        String resolvedToken=token.substring(7,token.length());
        if (jwtTokenProvider.validateToken(resolvedToken)) {
            String newToken=jwtTokenProvider.updateToken(resolvedToken);
            response.addHeader(HEADER, BEARER+newToken);
        }
        return ;
    }
    

    private String [] getEmailFromToken(String token) {
        String[] tokenEntry = token.split(":");
        return tokenEntry;
    }
    
    
}
