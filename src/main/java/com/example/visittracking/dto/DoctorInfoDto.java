package com.example.visittracking.dto;

import lombok.Data;

/**
 * @author Pavel Zhurenkov
 */
@Data
public class DoctorInfoDto {
    private String firstName;
    private String lastName;
    private Long totalPatients;
}
