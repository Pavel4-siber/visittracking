package com.example.visittracking.controller;

import com.example.visittracking.dto.*;
import com.example.visittracking.service.VisitTrackingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Pavel Zhurenkov
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VisitTrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitTrackingService visitTrackingService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateVisitRequest createVisitRequest;
    private VisitResponse visitResponse;
    private PatientVisitsResponse patientVisitsResponse;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        createVisitRequest = new CreateVisitRequest();
        createVisitRequest.setPatientId(1);
        createVisitRequest.setDoctorId(2);
        createVisitRequest.setStart("2025-11-15 10:00:00");
        createVisitRequest.setEnd("2025-11-15 11:00:00");

        visitResponse = new VisitResponse();
        visitResponse.setId(1);
        visitResponse.setPatientId(1);
        visitResponse.setDoctorId(2);
        visitResponse.setStart("2025-11-15 10:00:00");
        visitResponse.setEnd("2025-11-15 11:00:00");

        // Подготовка страницы с результатами

        DoctorInfoDto doctorInfoDto = new DoctorInfoDto();
        doctorInfoDto.setFirstName("Dr. Emily");
        doctorInfoDto.setLastName("White");
        doctorInfoDto.setTotalPatients(2L);

        LastVisitDto lastVisitDto = new LastVisitDto();
        lastVisitDto.setDoctor(doctorInfoDto);
        lastVisitDto.setStart("2025-11-15 10:00:00");
        lastVisitDto.setEnd("2025-11-15 11:00:00");

        PatientVisitDto patientVisitDto = new PatientVisitDto();
        patientVisitDto.setFirstName("Alice");
        patientVisitDto.setLastName("Smith");
        patientVisitDto.setLastVisits(of(lastVisitDto));

        patientVisitsResponse = new PatientVisitsResponse();
        patientVisitsResponse.setData(of(patientVisitDto));
        patientVisitsResponse.setCount(2L);
    }

    @Test
    void createVisit_ShouldReturnCreatedVisit() throws Exception {
        // Given
        when(visitTrackingService.createVisit(any(CreateVisitRequest.class)))
                .thenReturn(visitResponse);

        // When & Then
        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createVisitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patientId").value(1L))
                .andExpect(jsonPath("$.doctorId").value(2L));

        // Verify
        verify(visitTrackingService, times(1)).createVisit(any(CreateVisitRequest.class));
    }

    @Test
    void createVisit_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateVisitRequest invalidRequest = new CreateVisitRequest();

        // When & Then
        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPatientVisits_ShouldReturnPageOfVisits() throws Exception {
        // Given
        when(visitTrackingService.getPatientVisits(any(Pageable.class), eq("Ali"), any(List.class)))
                .thenReturn(patientVisitsResponse);

        // When & Then
        mockMvc.perform(get("/api/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", "Ali")
                        .param("doctorIds", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"data\":[{\"firstName\":\"Alice\",\"lastName\":\"Smith\"," +
                        "\"lastVisits\":[{\"start\":\"2025-11-15 10:00:00\",\"end\":\"2025-11-15 11:00:00\",\"doctor\"" +
                        ":{\"firstName\":\"Dr. Emily\",\"lastName\":\"White\",\"totalPatients\":2}}]}],\"count\":2}"));

        // Verify
        verify(visitTrackingService, times(1)).getPatientVisits(any(Pageable.class), eq("Ali"),
                any(List.class));
    }

    @Test
    void getPatientVisits_WithoutParams_ShouldReturnPageOfVisits() throws Exception {
        // Given
        when(visitTrackingService.getPatientVisits(any(Pageable.class), isNull(), isNull()))
                .thenReturn(patientVisitsResponse);

        // When & Then
        mockMvc.perform(get("/api/patients")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        // Verify
        verify(visitTrackingService, times(1)).getPatientVisits(any(Pageable.class), isNull(), isNull());
    }

    @Test
    void getPatientVisits_WithEmptyDoctorIds_ShouldReturnPageOfVisits() throws Exception {
        // Given
        when(visitTrackingService.getPatientVisits(any(Pageable.class), eq("search"), any(List.class)))
                .thenReturn(patientVisitsResponse);

        // When & Then
        mockMvc.perform(get("/api/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", "Ali")
                        .param("doctorIds", ""))  // Пустой список
                .andExpect(status().isOk());

        verify(visitTrackingService, times(1)).getPatientVisits(any(Pageable.class), eq("Ali"),
                any(List.class));
    }

    @Test
    void getPatientVisits_WithNullSearchAndDoctorIds_ShouldReturnPageOfVisits() throws Exception {
        // Given
        when(visitTrackingService.getPatientVisits(any(Pageable.class), isNull(), isNull()))
                .thenReturn(patientVisitsResponse);

        // When & Then
        mockMvc.perform(get("/api/patients")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(visitTrackingService, times(1)).getPatientVisits(any(Pageable.class), isNull(), isNull());
    }
}
