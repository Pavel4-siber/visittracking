package com.example.visittracking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Pavel Zhurenkov
 */
@Data
public class CreateVisitRequest {
    @NotNull
    private String start;
    @NotNull
    private String end;
    @NotNull
    private Integer patientId;
    @NotNull
    private Integer doctorId;
}
