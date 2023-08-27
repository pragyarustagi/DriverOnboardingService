package com.intuit.craftdemo.controller;


import com.intuit.craftdemo.config.ResponseConstants;
import com.intuit.craftdemo.dto.ResponseDto;
import com.intuit.craftdemo.dto.driver.ProfileUpdateDto;
import com.intuit.craftdemo.dto.driver.SignInDto;
import com.intuit.craftdemo.dto.driver.SignInResponseDto;
import com.intuit.craftdemo.dto.driver.SignupDto;
import com.intuit.craftdemo.exceptions.AuthenticationFailException;
import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.repository.DriverRepository;
import com.intuit.craftdemo.service.AuthenticationService;
import com.intuit.craftdemo.service.DriverDocumentService;
import com.intuit.craftdemo.exceptions.CustomException;
import com.intuit.craftdemo.service.DriverOnboardingBackgroundService;
import com.intuit.craftdemo.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("driver")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class DriverController {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    DriverService driverService;

    @Autowired
    DriverDocumentService documentService;

    @Autowired
    DriverOnboardingBackgroundService onboardingBackgroundService;

    // change func name
    @GetMapping("/fetchInfo")
    public List<Driver> findAllUser(@RequestParam(value = "token", required = true) String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        return driverRepository.findAll();
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> Signup(@Validated @RequestBody SignupDto signupDto, BindingResult bindingResult) throws CustomException {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + " " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(new ResponseDto(ResponseConstants.ERROR, errorMessage));
        }
        ResponseDto responseDto = driverService.signUp(signupDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/signIn")
    public SignInResponseDto SignIn(@RequestBody SignInDto signInDto) throws CustomException {
        return driverService.signIn(signInDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<ResponseDto> logout(@RequestParam(value = "token", required = true) String token) throws AuthenticationFailException {
        boolean success = authenticationService.invalidateToken(token);
        if (success) {
            return ResponseEntity.ok(new ResponseDto(ResponseConstants.SUCCESS, "Logout successful"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseDto(ResponseConstants.ERROR, "Logout failed"));
        }
    }

    @PutMapping("/profile/update")
    public ResponseEntity<ResponseDto> updateProfile(@RequestParam(value = "token", required = true) String token,
                                                     @RequestBody ProfileUpdateDto profileUpdateDto) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        Driver driver = authenticationService.getDriver(token);
        try {
            driverService.updateDriverProfile(driver, profileUpdateDto);
            return ResponseEntity.ok(new ResponseDto("success", "Profile updated successfully"));
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(new ResponseDto("error", e.getMessage()));
        }
    }

    @PostMapping("/onboard")
    public ResponseEntity<String> onboardDriver(@RequestParam(value = "token", required = true) String token,
                                                @RequestParam("files") MultipartFile[] files) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        Driver driver = authenticationService.getDriver(token);
        Map<String, List<MultipartFile>> groupedFiles = groupFilesByType(files);
        try {
            boolean documentsCollected = documentService.submitDocuments(driver, groupedFiles);
            if(documentsCollected) {
                boolean driverOnboarded = onboardingBackgroundService.completeOnboarding(driver);
                if (driverOnboarded) {
                    return ResponseEntity.ok("Driver onboarding completed successfully");
                } else {
                    return ResponseEntity.badRequest().body("Driver onboarding unsuccessful!");
                }
            }
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body("Driver document collection unsuccessful!");
        }
        return ResponseEntity.badRequest().body("Wrong input format");
    }

    private Map<String, List<MultipartFile>> groupFilesByType(MultipartFile[] files) {
        Map<String, List<MultipartFile>> groupedFiles = new HashMap<>();
        for (MultipartFile file : files) {
            String fileType = extractFileType(file.getOriginalFilename());
            groupedFiles.computeIfAbsent(fileType, k -> new ArrayList<>()).add(file);
        }
        return groupedFiles;
    }

    private String extractFileType(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        if (lowerCaseFileName.contains("aadharcard")) {
            return "aadharCard";
        } else if (lowerCaseFileName.contains("pancard")) {
            return "panCard";
        } else if (lowerCaseFileName.contains("drivinglicense")) {
            return "drivingLicense";
        } else if (lowerCaseFileName.contains("addressproof")) {
            return "addressProof";
        }
        throw new IllegalArgumentException("Unknown document type: " + fileName);
    }
}
