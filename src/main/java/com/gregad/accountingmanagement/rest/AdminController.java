package com.gregad.accountingmanagement.rest;

import com.gregad.accountingmanagement.service.interfaces.IRoleService;
import com.gregad.accountingmanagement.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.gregad.accountingmanagement.api.ApiConstants.*;

@RestController
public class AdminController {
    @Autowired
    IRoleService roleService;
    
    @PutMapping(value = PREFIX+"/{email}"+ROLE+"/{roleToAdd}")
    Set<String> addRole(@PathVariable String email,
                        @PathVariable String roleToAdd){
        return roleService.addRole(email,roleToAdd);
    }
    
    @DeleteMapping(value = PREFIX+"/{email}"+ROLE+"/{roleToRemove}")
    Set<String> removeRole(@PathVariable String email,
                           @PathVariable String roleToRemove){
        return roleService.removeRole(email,roleToRemove);
    }
}
