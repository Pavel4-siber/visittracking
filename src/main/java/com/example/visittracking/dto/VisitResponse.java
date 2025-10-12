package com.example.visittracking.dto;

import lombok.Data;

/**
 * @author Pavel Zhurenkov
 */
@Data
public class VisitResponse {
    private Integer id;
    private String start;
    private String end;
    private Integer patientId;
    private Integer doctorId;
}
