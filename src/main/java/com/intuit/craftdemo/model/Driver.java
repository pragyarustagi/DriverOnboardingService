package com.intuit.craftdemo.model;

import com.intuit.craftdemo.config.MessageStrings;
import com.intuit.craftdemo.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "drivers")
public class Driver implements User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String password;
    private String phoneNumber;
    private String dateOfBirth; // Add Date of Birth field
    private String gender;
    private String nationality;
    private String address;
    private String driverLicenseNumber;
    private String bankAccountNumber;
    private String vehicleType;
    private List<String> languagesSpoken;
    private String state;
    private List<DriverDocument> documents;

    // Constructors, getters, setters

    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(String dateOfBirth) {this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getNationality() { return nationality; }

    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getDriverLicenseNumber() { return driverLicenseNumber; }

    public void setDriverLicenseNumber(String driverLicenseNumber) { this.driverLicenseNumber = driverLicenseNumber; }

    public String getVehicleType() { return vehicleType; }

    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public List<String> getLanguagesSpoken() { return languagesSpoken; }

    public void setLanguagesSpoken(List<String> languagesSpoken) { this.languagesSpoken = languagesSpoken; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBankAccountNumber() { return bankAccountNumber; }

    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public List<DriverDocument> getDocuments() { return documents; }

    public void setDocuments(List<DriverDocument> documents) { this.documents = documents; }

    public Driver(String firstName, String lastName, String email, Role role, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.password = password;
        this.state = MessageStrings.PENDING;
        this.documents = new ArrayList<>();
    }
}
