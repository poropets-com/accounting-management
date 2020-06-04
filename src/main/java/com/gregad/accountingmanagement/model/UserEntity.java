package com.gregad.accountingmanagement.model;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity extends BaseEntity {

    @Id
    @Column(name = "email")
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "user_name")
    private String name;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "avatar")
    private String avatar;
    
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_email",referencedColumnName = "email")},
            inverseJoinColumns = {@JoinColumn(name = "role_name",referencedColumnName = "name")})
    private List<RoleEntity>roles;
    
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "user_favorites",
            joinColumns = {@JoinColumn(name = "user_email",referencedColumnName = "email")},
            inverseJoinColumns = {@JoinColumn(name = "post",referencedColumnName = "post_id")})
    private List<PostEntity>favorites;
}
