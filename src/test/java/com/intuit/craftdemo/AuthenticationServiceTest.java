package com.intuit.craftdemo;

import com.intuit.craftdemo.enums.Role;
import com.intuit.craftdemo.exceptions.AuthenticationFailException;
import com.intuit.craftdemo.model.AuthenticationToken;
import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.model.User;
import com.intuit.craftdemo.repository.TokenRepository;
import com.intuit.craftdemo.service.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authService;

    @Mock
    private TokenRepository repository;

    @Test
    public void testSaveConfirmationToken() {
        AuthenticationToken token = new AuthenticationToken();
        authService.saveConfirmationToken(token);
        verify(repository).save(token);
    }

    @Test
    public void testGetToken() {
        User user = new Driver("John", "Doe", "john@example.com", Role.DRIVER, "password");
        when(repository.findTokenByUser(user)).thenReturn(new AuthenticationToken());
        AuthenticationToken token = authService.getToken(user);
        assertNotNull(token);
        verify(repository).findTokenByUser(user);
    }

    @Test
    public void testGetDriverValidToken() {
        String tokenValue = "validToken";
        AuthenticationToken token = new AuthenticationToken();
        Driver driver = new Driver("John", "Doe", "john@example.com", Role.DRIVER, "password");
        token.setUser(driver);
        when(repository.findTokenByToken(tokenValue)).thenReturn(token);
        Driver retrievedDriver = authService.getDriver(tokenValue);
        assertEquals(driver, retrievedDriver);
        verify(repository).findTokenByToken(tokenValue);
    }

    @Test
    public void testGetDriverInvalidToken() {
        String invalidTokenValue = "invalidToken";
        when(repository.findTokenByToken(invalidTokenValue)).thenReturn(null);
        Driver retrievedDriver = authService.getDriver(invalidTokenValue);
        assertNull(retrievedDriver);
        verify(repository).findTokenByToken(invalidTokenValue);
    }

    @Test
    public void testAuthenticateValidToken() throws AuthenticationFailException {
        String validTokenValue = "validToken";
        AuthenticationToken token = new AuthenticationToken();
        Driver driver = new Driver("John", "Doe", "john@example.com", Role.DRIVER, "password");
        token.setUser(driver);
        when(repository.findTokenByToken(validTokenValue)).thenReturn(token);
        authService.authenticate(validTokenValue); // Should not throw any exception
        verify(repository).findTokenByToken(validTokenValue);
    }

    @Test(expected = AuthenticationFailException.class)
    public void testAuthenticateInvalidToken() throws AuthenticationFailException {
        String invalidTokenValue = "invalidToken";
        when(repository.findTokenByToken(invalidTokenValue)).thenReturn(null);
        authService.authenticate(invalidTokenValue);
    }

    @Test
    public void testInvalidateTokenValidToken() {
        String validTokenValue = "validToken";
        AuthenticationToken token = new AuthenticationToken();
        when(repository.findTokenByToken(validTokenValue)).thenReturn(token);
        boolean result = authService.invalidateToken(validTokenValue);
        assertTrue(result);
        verify(repository).findTokenByToken(validTokenValue);
        verify(repository).delete(token);
    }

    @Test
    public void testInvalidateTokenInvalidToken() {
        String invalidTokenValue = "invalidToken";
        when(repository.findTokenByToken(invalidTokenValue)).thenReturn(null);
        boolean result = authService.invalidateToken(invalidTokenValue);
        assertFalse(result);
        verify(repository).findTokenByToken(invalidTokenValue);
        verify(repository, never()).delete(any());
    }
}
