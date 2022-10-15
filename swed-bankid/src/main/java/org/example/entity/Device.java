package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {
    private String ipAddress;

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String toString() {
        return "Device{" +
                "ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
