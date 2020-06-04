package com.gregad.accountingmanagement.repository;

import com.gregad.accountingmanagement.model.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity,String> {

}
