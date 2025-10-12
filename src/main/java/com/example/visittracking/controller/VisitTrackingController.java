package com.example.visittracking.controller;

import com.example.visittracking.dto.CreateVisitRequest;
import com.example.visittracking.dto.PatientVisitsResponse;
import com.example.visittracking.dto.VisitResponse;
import com.example.visittracking.service.VisitTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Pavel Zhurenkov
 */
@RestController
@RequestMapping("/api")
@Tag(name = "User Management", description = "API для создания визитов/ получения информации о визитах пациентов")
public class VisitTrackingController {

    private final VisitTrackingService visitTrackingService;

    public VisitTrackingController(VisitTrackingService visitTrackingService) {
        this.visitTrackingService = visitTrackingService;
    }

    @PostMapping("/visits")
    @Operation(
            summary = "Создать визит пациента",
            description = "Возвращает информацию о созданном визите пациента"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Визит пациента создан")
    })
    public ResponseEntity<VisitResponse> createVisit(
            @Parameter(description = "CreateVisitRequest пациента", required = true)
            @Valid @RequestBody CreateVisitRequest request) {
        VisitResponse response = visitTrackingService.createVisit(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients")
    @Operation(
            summary = "Получить визиты пациента",
            description = "Возвращает информацию о визитах пациента по pageable, filter, ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получены визиты пациента")
    })
    public ResponseEntity<PatientVisitsResponse> getPatientVisits(
            @Parameter(description = "Параметры поиска визитов пациентов по ID", required = true)
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Integer> doctorIds) {

        PatientVisitsResponse response = visitTrackingService.getPatientVisits(pageable, search, doctorIds);
        return ResponseEntity.ok(response);
    }
}
