package com.example.visittracking.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Pavel Zhurenkov
 */
@Data
public class PatientVisitDto {
    private String firstName;
    private String lastName;
    private List<LastVisitDto> lastVisits;
}
