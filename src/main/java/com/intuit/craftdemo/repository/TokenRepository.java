package com.intuit.craftdemo.repository;

import com.intuit.craftdemo.model.AuthenticationToken;
import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends MongoRepository<AuthenticationToken, String> {
    AuthenticationToken findTokenByUser(User user);
    AuthenticationToken findTokenByToken(String token);
}
