package com.intuit.craftdemo;

import com.intuit.craftdemo.dto.ResponseDto;
import com.intuit.craftdemo.dto.driver.ProfileUpdateDto;
import com.intuit.craftdemo.dto.driver.SignInDto;
import com.intuit.craftdemo.dto.driver.SignupDto;
import com.intuit.craftdemo.enums.Role;
import com.intuit.craftdemo.exceptions.AuthenticationFailException;
import com.intuit.craftdemo.exceptions.CustomException;
import com.intuit.craftdemo.model.AuthenticationToken;
import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.repository.DriverRepository;
import com.intuit.craftdemo.service.AuthenticationService;
import com.intuit.craftdemo.service.DriverService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignUp_Success() throws CustomException, NoSuchAlgorithmException {
        SignupDto signupDto = new SignupDto("John", "Doe", "john@example.com", "password", "67474");
        when(driverRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
        when(driverRepository.save(any(Driver.class))).thenReturn(new Driver("John", "Doe", "john@example.com", Role.DRIVER, "password"));
        ResponseDto response = driverService.signUp(signupDto);
        assertEquals("success", response.getStatus());
        assertEquals("user created successfully", response.getMessage());
        verify(driverRepository, times(1)).findByEmail(signupDto.getEmail());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    public void testSignUp_UserAlreadyExists() {
        SignupDto signupDto = new SignupDto("John", "Doe", "john@example.com", "password", "234456");
        when(driverRepository.findByEmail(signupDto.getEmail())).thenReturn(new Driver("John", "Doe", "john@example.com", Role.DRIVER, "password"));
        assertThrows(CustomException.class, () -> driverService.signUp(signupDto));
    }

    @Test
    public void testSignIn_AlreadyLoggedIn() throws CustomException, NoSuchAlgorithmException {
        SignInDto signInDto = new SignInDto("john@example.com", "password");
        String hashedPassword = driverService.hashPassword(signInDto.getPassword());
        Driver driver = new Driver("John", "Doe", "john@example.com", Role.DRIVER, hashedPassword);
        when(driverRepository.findByEmail(signInDto.getEmail())).thenReturn(driver);
        AuthenticationToken authenticationToken = new AuthenticationToken(driver);
        when(authenticationService.getToken(driver)).thenReturn(authenticationToken);
        assertThrows(CustomException.class, () -> {
            driverService.signIn(signInDto);
        });
        verify(driverRepository, times(1)).findByEmail(signInDto.getEmail());
        verify(authenticationService, times(1)).getToken(driver);
    }


    @Test
    public void testSignIn_DriverNotFound() {
        SignInDto signInDto = new SignInDto("john@example.com", "password");
        when(driverRepository.findByEmail(signInDto.getEmail())).thenReturn(null);
        assertThrows(AuthenticationFailException.class, () -> driverService.signIn(signInDto));
    }

    @Test
    public void testUpdateDriverProfile_Success() throws CustomException {
        Driver driver = new Driver("John", "Doe", "john@example.com", Role.DRIVER, "hashed_password");
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        driverService.updateDriverProfile(driver, profileUpdateDto);
        verify(driverRepository, times(1)).save(driver);
    }

}
