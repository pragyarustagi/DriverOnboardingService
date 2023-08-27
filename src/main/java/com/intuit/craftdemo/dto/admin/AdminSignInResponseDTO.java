package com.intuit.craftdemo.dto.admin;

public class AdminSignInResponseDTO {

    private String status;
    private String token;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AdminSignInResponseDTO(String status, String token) {
        this.status = status;
        this.token = token;
    }
}
