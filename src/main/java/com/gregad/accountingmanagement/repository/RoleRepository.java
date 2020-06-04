package com.gregad.accountingmanagement.repository;

import com.gregad.accountingmanagement.model.Role;
import com.gregad.accountingmanagement.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity,String> {
    RoleEntity findByName(Role role);
}
