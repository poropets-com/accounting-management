package com.gregad.accountingmanagement.repository;

import com.gregad.accountingmanagement.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,String> {
}
