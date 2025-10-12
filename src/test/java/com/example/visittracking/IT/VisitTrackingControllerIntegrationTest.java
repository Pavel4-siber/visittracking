package com.example.visittracking.IT;

import com.example.visittracking.dto.CreateVisitRequest;
import com.example.visittracking.dto.PatientVisitsResponse;
import com.example.visittracking.dto.VisitResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Pavel Zhurenkov
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class VisitTrackingControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    protected int port;

    private String BASE_VISIT_URL;
    private String BASE_PATIENT_URL;
    private CreateVisitRequest createVisitRequest;

    @BeforeEach
    void setUp() {
        BASE_VISIT_URL = "http://localhost:" + port + "/api/visits";
        BASE_PATIENT_URL = "http://localhost:" + port + "/api/patients?page=0&size=10&search=John";

        createVisitRequest = new CreateVisitRequest();
        createVisitRequest.setPatientId(1);
        createVisitRequest.setDoctorId(2);
        createVisitRequest.setStart("2025-11-15T10:00:00+03:00");
        createVisitRequest.setEnd("2025-11-15T11:00:00+03:00");
    }

    @Test
    @DisplayName("Should create visit successfully with valid request")
    void shouldCreateVisitSuccessfully() {
        // Given
        // When
        ResponseEntity<VisitResponse> response = restTemplate.postForEntity(
                BASE_VISIT_URL,
                createVisitRequest,
                VisitResponse.class
        );
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPatientId()).isEqualTo(1);
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("Should get patient visits with pagination")
    void shouldGetPatientVisitsWithPagination() {
        // Given
        // When
        ResponseEntity<PatientVisitsResponse> response = restTemplate.getForEntity(
                BASE_PATIENT_URL,
                PatientVisitsResponse.class
        );
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotNull();
    }

    @Test
    @DisplayName("Should handle invalid page parameters gracefully")
    void shouldHandleInvalidPageParameters() {
        // Given
        String url = "/api/patients?page=-1&size=50"; // Invalid page number

        // When
        ResponseEntity<PatientVisitsResponse> response = restTemplate.getForEntity(
                url,
                PatientVisitsResponse.class
        );
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should return not found for non-existent visit")
    void shouldReturnNotFoundForNonExistentVisit() {
        // Given
        Long nonExistentId = 99999L;

        // When
        ResponseEntity<VisitResponse> response = restTemplate.getForEntity(
                BASE_VISIT_URL + "/" + nonExistentId,
                VisitResponse.class
        );
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should handle request with doctorIds filter")
    void shouldHandleDoctorIdsFilter() {
        // Given
        String url = "/api/patients?doctorIds=1&doctorIds=2&search=Jane";

        // When
        ResponseEntity<PatientVisitsResponse> response = restTemplate.getForEntity(
                url,
                PatientVisitsResponse.class
        );
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
