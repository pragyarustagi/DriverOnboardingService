package com.intuit.craftdemo.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.UUID;

@Document(collection = "tokens")
public class AuthenticationToken {

    @Id
    private String id;

    @Field(name = "token")
    private String token;

    @Field(name = "created_date")
    private Date createdDate;

    @Field(name = "userID")
    private User user;

    public AuthenticationToken(User user) {
        this.user = user;
        this.createdDate = new Date();
        this.token = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(Driver user) {
        this.user = user;
    }

    public AuthenticationToken(String id, String token, Date createdDate, Driver user) {
        this.id = id;
        this.token = token;
        this.createdDate = createdDate;
        this.user = user;
    }

    public AuthenticationToken() {
    }
}
