package com.gregad.accountingmanagement.rest;

import com.gregad.accountingmanagement.dto.responseDto.BlockUserResponseDto;
import com.gregad.accountingmanagement.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import static com.gregad.accountingmanagement.api.ApiConstants.*;

@RestController
public class ModeratorController {
    
    @Autowired
    IUserService userService;
    
    @PutMapping(value = PREFIX+"/{email}"+BLOCK+"/{blockValue}")
    BlockUserResponseDto blockUser(@RequestHeader(name="Authorization") String token,
                                   @PathVariable String email,
                                   @PathVariable boolean blockValue){
        return userService.blockUser(email,blockValue,token);
    }
}
