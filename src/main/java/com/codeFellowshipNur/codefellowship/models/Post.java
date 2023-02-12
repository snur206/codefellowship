package com.codeFellowshipNur.codefellowship.models;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Post {
    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @Lob @Type(type ="org.hibernate.type.TextType")
    private String body;
    private Date createdAt;

    @ManyToOne
    private ApplicationUser createdBy;
    // Required to add comments to post
//    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL)

    protected Post() {
    }

    public Post(String body, Date createdAt, ApplicationUser createdBy) {
        this.body = body;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public ApplicationUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ApplicationUser createdBy) {
        this.createdBy = createdBy;
    }
}
