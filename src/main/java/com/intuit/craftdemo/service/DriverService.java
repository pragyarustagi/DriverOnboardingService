package com.intuit.craftdemo.service;


import com.intuit.craftdemo.config.MessageStrings;
import com.intuit.craftdemo.dto.ResponseDto;
import com.intuit.craftdemo.dto.driver.ProfileUpdateDto;
import com.intuit.craftdemo.dto.driver.SignInDto;
import com.intuit.craftdemo.dto.driver.SignInResponseDto;
import com.intuit.craftdemo.dto.driver.SignupDto;
import com.intuit.craftdemo.enums.ResponseStatus;
import com.intuit.craftdemo.enums.Role;
import com.intuit.craftdemo.exceptions.AuthenticationFailException;
import com.intuit.craftdemo.model.AuthenticationToken;
import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.repository.DriverRepository;
import com.intuit.craftdemo.utils.Helper;
import com.intuit.craftdemo.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class DriverService {

    @Autowired
    DriverRepository driverRepository;
    @Autowired
    AuthenticationService authenticationService;
    Logger logger = LoggerFactory.getLogger(DriverService.class);
    public ResponseDto signUp(SignupDto signupDto)  throws CustomException {
        // Check to see if the current email address has already been registered.
        if (Helper.notNull(driverRepository.findByEmail(signupDto.getEmail()))) {
            // If the email address has been registered then throw an exception.
            throw new CustomException("User already exists");
        }
        // first encrypt the password
        String encryptedPassword = signupDto.getPassword();
        try {
            encryptedPassword = hashPassword(signupDto.getPassword());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
        }
        Driver driver = new Driver(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(), Role.driver, encryptedPassword );
        Driver createdDriver;
        try {
            createdDriver = driverRepository.save(driver);
            // generate token for driver
            final AuthenticationToken authenticationToken = new AuthenticationToken(createdDriver);
            // save token in database
            authenticationService.saveConfirmationToken(authenticationToken);
            // success in creating
            return new ResponseDto(ResponseStatus.success.toString(), MessageStrings.USER_CREATED);
        } catch (Exception e) {
            // handle signup error
            throw new CustomException(e.getMessage());
        }
    }

    public SignInResponseDto signIn(SignInDto signInDto) throws CustomException {
        // first find User by email
        Driver driver = driverRepository.findByEmail(signInDto.getEmail());
        if(!Helper.notNull(driver)){
            throw  new AuthenticationFailException("driver not present");
        }
        try {
            if (!driver.getPassword().equals(hashPassword(signInDto.getPassword()))){
                // passowrd doesnot match
                throw  new AuthenticationFailException(MessageStrings.WRONG_PASSWORD);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        AuthenticationToken token = authenticationService.getToken(driver);
        if(Helper.notNull(token)) {
            throw new CustomException(("already logged in!"));
        }
        AuthenticationToken authenticationToken = new AuthenticationToken(driver);
        // save token in database
        authenticationService.saveConfirmationToken(authenticationToken);
        AuthenticationToken new_token = authenticationService.getToken(driver);
        return new SignInResponseDto ("success", new_token.getToken());
    }


    public String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        return myHash;
    }

    public void updateDriverProfile(Driver driver, ProfileUpdateDto profileUpdateDto) throws CustomException {
        // Update the driver's profile based on the profileUpdateDto
        driver.setFirstName(profileUpdateDto.getFirstName());
        driver.setLastName(profileUpdateDto.getLastName());
        driver.setDriverLicenseNumber(profileUpdateDto.getDrivingLicenseNumber());
        driver.setAddress(profileUpdateDto.getAddress());
        driver.setGender(profileUpdateDto.getGender());
        driver.setDateOfBirth(profileUpdateDto.getDateOfBirth());
        driver.setVehicleType(profileUpdateDto.getVehicleType());
        driver.setLanguagesSpoken(profileUpdateDto.getLanguagesSpoken());
        driver.setPhoneNumber(profileUpdateDto.getPhoneNumber());
        driver.setBankAccountNumber(profileUpdateDto.getBankAccountNumber());
        driver.setNationality(profileUpdateDto.getNationality());
        driverRepository.save(driver);
    }

}
