package com.example.spirituality_be.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OnboardingRequest {
    private String fullName;
    private LocalDate dob;
    private Byte hour;
    private Byte minute;
}