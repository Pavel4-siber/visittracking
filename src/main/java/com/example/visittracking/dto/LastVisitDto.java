package com.example.visittracking.dto;

import lombok.Data;

/**
 * @author Pavel Zhurenkov
 */
@Data
public class LastVisitDto {
    private String start;
    private String end;
    private DoctorInfoDto doctor;
}
