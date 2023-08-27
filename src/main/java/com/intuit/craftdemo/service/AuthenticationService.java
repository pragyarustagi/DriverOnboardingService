package com.intuit.craftdemo.service;

import com.intuit.craftdemo.model.AuthenticationToken;
import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.model.User;
import com.intuit.craftdemo.repository.TokenRepository;
import com.intuit.craftdemo.config.MessageStrings;
import com.intuit.craftdemo.exceptions.AuthenticationFailException;
import com.intuit.craftdemo.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    TokenRepository repository;

    public void saveConfirmationToken(AuthenticationToken authenticationToken) {
        repository.save(authenticationToken);
    }

    public AuthenticationToken getToken(User user) {
        return repository.findTokenByUser(user);
    }

    public Driver getDriver(String token) {
        AuthenticationToken authenticationToken = repository.findTokenByToken(token);
        if (Helper.notNull(authenticationToken)) {
            if (Helper.notNull(authenticationToken.getUser())) {
                return (Driver) authenticationToken.getUser();
            }
        }
        return null;
    }

    public void authenticate(String token) throws AuthenticationFailException {
        if (!Helper.notNull(token)) {
            throw new AuthenticationFailException(MessageStrings.AUTH_TOKEN_NOT_PRESENT);
        }
        if (!Helper.notNull(getDriver(token))) {
            throw new AuthenticationFailException(MessageStrings.AUTH_TOKEN_NOT_VALID);
        }
    }

    public Boolean invalidateToken(String token) {
        AuthenticationToken authenticationToken = repository.findTokenByToken(token);
        if (Helper.notNull(authenticationToken)) {
            repository.delete(authenticationToken);
            return true;
        }
        return false;
    }
}
