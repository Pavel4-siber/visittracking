package com.example.visittracking.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Pavel Zhurenkov
 */
@Data
public class PatientVisitsResponse {
    private List<PatientVisitDto> data;
    private Long count;
}
