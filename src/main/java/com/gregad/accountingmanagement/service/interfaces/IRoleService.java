package com.gregad.accountingmanagement.service.interfaces;

import java.util.Set;

public interface IRoleService {

    Set<String> addRole(String email, String role);
    Set<String> removeRole(String email, String role);
}
