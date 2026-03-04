package com.example.spirituality_be.dto;

import lombok.Data;
import java.util.List;

@Data
public class DailyEnergyDto {
    private Integer mentalEnergy;
    private Integer emotionalBalance;
    private Integer focusLevel;
    private Integer actionReadiness;
    private Integer socialHarmony;
    private Integer totalEnergy;
    private String menh;
    private String affirmation;
    private String userElement;
    private String dailyElement;
    private Integer interactionScore;
    private List<Integer> radarData;
    private Boolean fromCache;
}