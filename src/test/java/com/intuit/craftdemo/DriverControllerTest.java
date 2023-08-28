package com.intuit.craftdemo;

import com.intuit.craftdemo.controller.DriverController;
import com.intuit.craftdemo.dto.ResponseDto;
import com.intuit.craftdemo.dto.driver.ProfileUpdateDto;
import com.intuit.craftdemo.dto.driver.SignInDto;
import com.intuit.craftdemo.dto.driver.SignInResponseDto;
import com.intuit.craftdemo.dto.driver.SignupDto;
import com.intuit.craftdemo.enums.Role;
import com.intuit.craftdemo.exceptions.AuthenticationFailException;
import com.intuit.craftdemo.exceptions.CustomException;
import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.repository.DriverRepository;
import com.intuit.craftdemo.service.AuthenticationService;
import com.intuit.craftdemo.service.DriverDocumentService;
import com.intuit.craftdemo.service.DriverOnboardingBackgroundService;
import com.intuit.craftdemo.service.DriverService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DriverControllerTest {

    @InjectMocks
    private DriverController driverController;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private DriverService driverService;

    @Mock
    private DriverDocumentService documentService;

    @Mock
    private DriverOnboardingBackgroundService onboardingBackgroundService;

    @Mock
    private DriverRepository driverRepository;

    @Test
    public void testFetchDriverInfo_ValidToken_ReturnsListOfDrivers() throws AuthenticationFailException {
        String token = "valid_token";
        List<Driver> drivers = new ArrayList<>(); // Create sample drivers
        doNothing().when(authenticationService).authenticate(token);
        when(driverRepository.findAll()).thenReturn(drivers);
        List<Driver> result = driverController.fetchDriverInfo(token);
        assertEquals(drivers, result);
    }


    @Test
    public void testLogout_ValidToken_SuccessfulLogout() throws AuthenticationFailException {
        String token = "valid_token";
        when(authenticationService.invalidateToken(token)).thenReturn(true);
        ResponseEntity<ResponseDto> response = driverController.logout(token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
    }

    @Test
    public void testLogout_InvalidToken_FailedLogout() throws AuthenticationFailException {
        String token = "invalid_token";
        when(authenticationService.invalidateToken(token)).thenReturn(false);
        ResponseEntity<ResponseDto> response = driverController.logout(token);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
    }

    @Test
    public void testUpdateProfile_ValidTokenAndDto_SuccessfulProfileUpdate() throws AuthenticationFailException, CustomException {
        String token = "valid_token";
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto(/* populate fields */);
        Driver driver = new Driver("John", "Doe", "john@example.com", Role.DRIVER, "password");
        doNothing().when(authenticationService).authenticate(token);
        when(authenticationService.getDriver(token)).thenReturn(driver);
        doNothing().when(driverService).updateDriverProfile(driver, profileUpdateDto);
        ResponseEntity<ResponseDto> response = driverController.updateProfile(token, profileUpdateDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
    }

    @Test
    public void testUpdateProfile_ValidTokenAndDto_CustomException() throws AuthenticationFailException, CustomException {
        String token = "valid_token";
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto(/* populate fields */);
        Driver driver = new Driver("John", "Doe", "john@example.com", Role.DRIVER, "password");
        doNothing().when(authenticationService).authenticate(token);
        when(authenticationService.getDriver(token)).thenReturn(driver);
        doThrow(new CustomException("Profile update failed")).when(driverService).updateDriverProfile(driver, profileUpdateDto);
        ResponseEntity<ResponseDto> response = driverController.updateProfile(token, profileUpdateDto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
        assertEquals("Profile update failed", response.getBody().getMessage());
    }

    @Test
    public void testSignIn_ValidCredentials_Success() throws CustomException {
        SignInDto signInDto = new SignInDto("valid@example.com", "password");
        SignInResponseDto responseDto = new SignInResponseDto("success", "valid_token");
        when(driverService.signIn(signInDto)).thenReturn(responseDto);
        SignInResponseDto response = driverController.SignIn(signInDto);
        assertNotNull(response);
    }

    @Test
    public void testSignIn_InvalidCredentials_CustomException() throws CustomException {
        SignInDto signInDto = new SignInDto("invalid@example.com", "wrong_password");
        when(driverService.signIn(signInDto)).thenThrow(new CustomException("Invalid credentials"));
        assertThrows(CustomException.class, () -> driverController.SignIn(signInDto));
    }

    @Test
    public void testSignup_ValidInput_Success() throws CustomException {
        SignupDto signupDto = new SignupDto("John", "Doe", "valid@example.com", "password", "1234567890");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        ResponseDto responseDto = new ResponseDto("success", "User registered successfully");
        when(driverService.signUp(signupDto)).thenReturn(responseDto);
        ResponseEntity<ResponseDto> response = driverController.Signup(signupDto, bindingResult);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("User registered successfully", response.getBody().getMessage());
    }

    @Test
    public void testSignup_InvalidInput_BindingResultErrors() throws CustomException {
        SignupDto signupDto = new SignupDto("John", "Doe", "invalid-email", "password", "1234567890");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        FieldError fieldError = new FieldError("signupDto", "email", "Invalid email format");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        ResponseEntity<ResponseDto> response = driverController.Signup(signupDto, bindingResult);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
    }

    @Test
    public void testOnboardDriver_DocumentsCollected_Unsuccessful_DocumentCollection() throws AuthenticationFailException, CustomException {
        // Arrange
        String token = "valid_token";
        MultipartFile mockFile1 = new MockMultipartFile("aadharcard.jpg", "aadharcard.jpg", null, new byte[0]);
        MultipartFile mockFile2 = new MockMultipartFile("pancard.zip", "pancard.zip", null, new byte[0]);
        MultipartFile[] files = {mockFile1, mockFile2};
        Driver driver = new Driver("John", "Doe", "john@example.com", Role.DRIVER, "password");
        doNothing().when(authenticationService).authenticate(token);
        when(authenticationService.getDriver(token)).thenReturn(driver);
        when(documentService.submitDocuments(eq(driver), any())).thenReturn(false);
        ResponseEntity<String> response = driverController.onboardDriver(token, files);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Wrong input format", response.getBody());
    }

    @Test
    public void testOnboardDriver_DocumentsCollected_Successful_Onboarding() throws AuthenticationFailException, CustomException {
        String token = "valid_token";
        MultipartFile mockFile1 = new MockMultipartFile("aadharcard.jpg", "aadharcard.jpg", null, new byte[0]);
        MultipartFile[] files = {mockFile1};
        Driver driver = new Driver("John", "Doe", "john@example.com", Role.DRIVER, "password");
        when(authenticationService.getDriver(token)).thenReturn(driver);
        when(documentService.submitDocuments(eq(driver), any())).thenReturn(true);
        when(onboardingBackgroundService.completeOnboarding(driver)).thenReturn(true);
        ResponseEntity<String> response = driverController.onboardDriver(token, files);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Driver onboarding completed successfully", response.getBody());
    }

    @Test
    public void testOnboardDriver_DocumentsCollected_Unsuccessful_Onboarding() throws AuthenticationFailException, CustomException {
        String token = "valid_token";
        MultipartFile mockFile1 = new MockMultipartFile("aadharcard.jpg", "aadharcard.jpg", null, new byte[0]);
        MultipartFile[] files = {mockFile1};
        Driver driver = new Driver("John", "Doe", "john@example.com", Role.DRIVER, "password");
        when(authenticationService.getDriver(token)).thenReturn(driver);
        when(documentService.submitDocuments(eq(driver), any())).thenReturn(true);
        when(onboardingBackgroundService.completeOnboarding(driver)).thenReturn(false);
        ResponseEntity<String> response = driverController.onboardDriver(token, files);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Driver onboarding unsuccessful!", response.getBody());
    }


}
