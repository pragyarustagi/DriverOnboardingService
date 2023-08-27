package com.intuit.craftdemo.dto.driver;

import java.util.*;

public class ProfileUpdateDto {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String drivingLicenseNumber;
    private String bankAccountNumber;
    private String dateOfBirth;
    private String gender;
    private String nationality;
    private String address;
    private String vehicleType;
    private List<String> languagesSpoken;

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) { this.drivingLicenseNumber = drivingLicenseNumber; }

    public String getBankAccountNumber() { return bankAccountNumber; }

    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getNationality() { return nationality; }

    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getVehicleType() { return vehicleType; }

    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public List<String> getLanguagesSpoken() { return languagesSpoken; }

    public void setLanguagesSpoken(List<String> languagesSpoken) { this.languagesSpoken = languagesSpoken; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
