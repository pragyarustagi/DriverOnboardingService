package com.intuit.craftdemo.controller;

import com.intuit.craftdemo.config.ResponseConstants;
import com.intuit.craftdemo.dto.ResponseDto;
import com.intuit.craftdemo.dto.admin.AdminSignInDTO;
import com.intuit.craftdemo.dto.admin.AdminSignInResponseDTO;
import com.intuit.craftdemo.dto.admin.AdminSignupDTO;
import com.intuit.craftdemo.exceptions.AuthenticationFailException;
import com.intuit.craftdemo.exceptions.CustomException;
import com.intuit.craftdemo.model.Admin;
import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.repository.AdminRepository;
import com.intuit.craftdemo.repository.DriverRepository;
import com.intuit.craftdemo.service.AdminService;
import com.intuit.craftdemo.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class AdminController {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AdminService adminService;


    // change func name
    @GetMapping("/fetchInfo")
    public List<Admin> findAllUser(@RequestParam(value = "token", required = true) String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        return adminRepository.findAll();
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> Signup(@Validated @RequestBody AdminSignupDTO signupDto, BindingResult bindingResult) throws CustomException {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + " " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(new ResponseDto(ResponseConstants.ERROR, errorMessage));
        }
        ResponseDto responseDto = adminService.signUp(signupDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/signIn")
    public AdminSignInResponseDTO SignIn(@RequestBody AdminSignInDTO signInDto) throws CustomException {
        return adminService.signIn(signInDto);
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

    @GetMapping("/fetch/drivers")
    public ResponseEntity<Page<Driver>> getAllDrivers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Driver> driversPage = driverRepository.findAll(pageable);
        return ResponseEntity.ok(driversPage);
    }
}
