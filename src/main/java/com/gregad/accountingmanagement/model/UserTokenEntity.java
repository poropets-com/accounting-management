package com.gregad.accountingmanagement.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_token")
@Data
@Getter
@Setter
public class UserTokenEntity {
    
    @Id
    @Column(name = "user_email")
    private String email;
    
    @Column(name = "current_token")
    private String token;

    public UserTokenEntity() {
    }

    public UserTokenEntity(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
