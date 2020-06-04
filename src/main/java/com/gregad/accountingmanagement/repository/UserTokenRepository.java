package com.gregad.accountingmanagement.repository;

import com.gregad.accountingmanagement.model.UserTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTokenRepository extends JpaRepository<UserTokenEntity,String> {
}
