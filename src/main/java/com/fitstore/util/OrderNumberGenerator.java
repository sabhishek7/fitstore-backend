package com.fitstore.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class OrderNumberGenerator {

    /**
     * Generates a human-readable order number in the format: HFP-YYYYMMDD-XXXX
     * Example: HFP-20260414-A3B4
     */
    public String generate() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "HFP-" + date + "-" + suffix;
    }
}
