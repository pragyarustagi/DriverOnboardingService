package com.intuit.craftdemo.service;

import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.repository.DriverRepository;
import com.intuit.craftdemo.config.MessageStrings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DriverContext {

    @Autowired
    DriverRepository driverRepository;
    public void markReady(Driver driver) {
        driver.setState(MessageStrings.VERIFIED);
        driverRepository.save(driver);
    }
}
