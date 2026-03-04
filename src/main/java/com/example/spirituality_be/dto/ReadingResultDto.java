package com.example.spirituality_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingResultDto {
    private Integer tarotId;
    private String tarotName;
    private String tarotImage;
    private String tarotMeaning;

    private Integer hexId;
    @Builder.Default
    private String hexName = "";
    @Builder.Default
    private Integer hexNumber = 0;
    @Builder.Default
    private String hexImage = "";
    @Builder.Default
    private String hexBinary = "111111";
    @Builder.Default
    private String hexMeaning = "";

    private String aiInterpretation;
    private Integer remainingTurns;
    private Boolean fromCache;
}