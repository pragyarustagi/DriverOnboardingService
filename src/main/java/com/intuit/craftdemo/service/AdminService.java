package com.intuit.craftdemo.service;

import com.intuit.craftdemo.config.MessageStrings;
import com.intuit.craftdemo.dto.ResponseDto;
import com.intuit.craftdemo.dto.admin.AdminSignInDTO;
import com.intuit.craftdemo.dto.admin.AdminSignInResponseDTO;
import com.intuit.craftdemo.dto.admin.AdminSignupDTO;
import com.intuit.craftdemo.dto.driver.SignInDto;
import com.intuit.craftdemo.dto.driver.SignInResponseDto;
import com.intuit.craftdemo.dto.driver.SignupDto;
import com.intuit.craftdemo.enums.ResponseStatus;
import com.intuit.craftdemo.enums.Role;
import com.intuit.craftdemo.exceptions.AuthenticationFailException;
import com.intuit.craftdemo.exceptions.CustomException;
import com.intuit.craftdemo.model.Admin;
import com.intuit.craftdemo.model.AuthenticationToken;
import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.repository.AdminRepository;
import com.intuit.craftdemo.repository.DriverRepository;
import com.intuit.craftdemo.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class AdminService {

    @Autowired
    AdminRepository adminRepository;
    @Autowired
    AuthenticationService authenticationService;
    Logger logger = LoggerFactory.getLogger(DriverService.class);
    public ResponseDto signUp(AdminSignupDTO signupDto)  throws CustomException {
        // Check to see if the current email address has already been registered.
        if (Helper.notNull(adminRepository.findByEmail(signupDto.getEmail()))) {
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
        Admin admin = new Admin(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(), Role.admin, encryptedPassword );
        Admin createdDriver;
        try {
            createdDriver = adminRepository.save(admin);
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

    public AdminSignInResponseDTO signIn(AdminSignInDTO signInDto) throws CustomException {
        // first find User by email
        Admin admin = adminRepository.findByEmail(signInDto.getEmail());
        if(!Helper.notNull(admin)){
            throw  new AuthenticationFailException("admin not present");
        }
        try {
            if (!admin.getPassword().equals(hashPassword(signInDto.getPassword()))){
                // passowrd doesnot match
                throw  new AuthenticationFailException(MessageStrings.WRONG_PASSWORD);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        AuthenticationToken token = authenticationService.getToken(admin);
        if(Helper.notNull(token)) {
            throw new CustomException(("already logged in!"));
        }
        AuthenticationToken authenticationToken = new AuthenticationToken(admin);
        // save token in database
        authenticationService.saveConfirmationToken(authenticationToken);
        AuthenticationToken new_token = authenticationService.getToken(admin);
        return new AdminSignInResponseDTO ("success", new_token.getToken());
    }


    public String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        return myHash;
    }
}
