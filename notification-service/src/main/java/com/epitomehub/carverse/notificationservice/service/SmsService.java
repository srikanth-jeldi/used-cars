package com.epitomehub.carverse.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Stub SMS service. Replace with real Twilio or other provider later.
 */
@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    public void sendSms(String phone, String message) {
        // TODO: integrate with real SMS provider (Twilio, AWS SNS, etc.)
        log.info("SIMULATED SMS to {}: {}", phone, message);
    }
}
