package com.epitomehub.carverse.authservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory OTP store.
 * Maps: otpCode -> identifier (email or phone)
 *
 * NOTE: OTPs will be lost if the application restarts.
 * For production, replace this with Redis or a database.
 */
@Service
public class OtpService {

    // key = otpCode, value = identifier (email/phone)
    private final Map<String, String> otpMap = new ConcurrentHashMap<>();

    public void storeOtp(String identifier, String otpCode) {
        otpMap.put(otpCode, identifier);
    }

    public String getIdentifierByOtp(String otpCode) {
        return otpMap.get(otpCode);
    }

    public void removeOtp(String otpCode) {
        otpMap.remove(otpCode);
    }
}
