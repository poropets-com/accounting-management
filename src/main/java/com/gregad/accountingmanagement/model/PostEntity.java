package com.gregad.accountingmanagement.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
public class PostEntity {
    @Id
    @Column(name = "post_id")
    private String postId;

    @ManyToMany(mappedBy = "favorites",fetch = FetchType.EAGER)
    private List<UserEntity>users;

    public PostEntity() {
    }

    public PostEntity(String id){
        this.postId=id;
    }
}
