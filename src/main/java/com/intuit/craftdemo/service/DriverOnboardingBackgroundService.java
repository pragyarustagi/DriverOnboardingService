package com.intuit.craftdemo.service;

import com.intuit.craftdemo.model.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverOnboardingBackgroundService {

    private BackgroundVerification backgroundVerification;
    private TrackingDeviceShipping trackingDeviceShipping;
    private DriverContext driverContext;

    public DriverOnboardingBackgroundService(BackgroundVerification backgroundVerification,
                                             TrackingDeviceShipping trackingDeviceShipping,
                                             DriverContext driverContext) {
        this.backgroundVerification = backgroundVerification;
        this.trackingDeviceShipping = trackingDeviceShipping;
        this.driverContext = driverContext;
    }

    public boolean completeOnboarding(Driver driver) {
        boolean backgroundVerified = backgroundVerification.verifyBackground();
        boolean deviceShipped = trackingDeviceShipping.shipDevice();

        if (backgroundVerified && deviceShipped) {
            driverContext.markReady(driver);
        }
        return backgroundVerified && deviceShipped;
    }
}
