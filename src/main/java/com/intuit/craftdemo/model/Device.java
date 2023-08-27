package com.intuit.craftdemo.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "device")
public class Device {
    private String deviceId;
    private String serialNumber;
    private boolean isShipped;
    private String driverId;

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getSerialNumber() { return serialNumber; }

    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public boolean isShipped() { return isShipped; }

    public void setShipped(boolean shipped) { isShipped = shipped; }

    public String getDriverId() { return driverId; }

    public void setDriverId(String driverId) { this.driverId = driverId; }
}
