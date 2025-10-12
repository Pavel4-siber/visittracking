package com.example.visittracking.service;

import com.example.visittracking.TestBase;
import com.example.visittracking.dto.*;
import com.example.visittracking.entity.Doctor;
import com.example.visittracking.entity.Patient;
import com.example.visittracking.entity.Visit;
import com.example.visittracking.exception.custom.ConflictResourceException;
import com.example.visittracking.exception.custom.DateTimeNotValidException;
import com.example.visittracking.exception.custom.ResourceNotFoundException;
import com.example.visittracking.repository.DoctorRepository;
import com.example.visittracking.repository.PatientRepository;
import com.example.visittracking.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Pavel Zhurenkov
 */

@Transactional
public class VisitTrackingServiceIntegrationTest extends TestBase {

    @Autowired
    private VisitTrackingService visitTrackingService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private VisitRepository visitRepository;

    private Patient patient1;
    private Patient patient2;
    private Doctor doctor1;
    private Doctor doctor2;

    @BeforeEach
    void setUp() {

        patient1 = patientRepository.findById(1).get();

        patient2 = patientRepository.findById(2).get();

        doctor1 = doctorRepository.findById(1).get();

        doctor2 = doctorRepository.findById(2).get();
    }

    private String toUtcIsoString(LocalDateTime localDateTime, String timeZoneId) {
        return ZonedDateTime.of(localDateTime, doctorRepository.findById(doctor1.getId()).get().getTimezone()
                        .equals(timeZoneId) ? doctor1.getTimezone().equals("America/New_York")
                        ? java.time.ZoneId.of("America/New_York") : java.time.ZoneId.of("Europe/London")
                        : java.time.ZoneId.of("Europe/London"))
                .withZoneSameInstant(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);
    }

    @Test
    void createVisit_Success() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 26, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 26, 10, 30, 0);

        CreateVisitRequest request = new CreateVisitRequest();
        request.setPatientId(patient1.getId());
        request.setDoctorId(doctor1.getId());
        request.setStart(toUtcIsoString(start, doctor1.getTimezone())); // Пример UTC-строки
        request.setEnd(toUtcIsoString(end, doctor1.getTimezone()));     // Пример UTC-строки

        VisitResponse response = visitTrackingService.createVisit(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getPatientId()).isEqualTo(patient1.getId());
        assertThat(response.getDoctorId()).isEqualTo(doctor1.getId());
        // Проверяем, что дата/время хранится корректно (с учетом конвертации в LocalDateTime)

        Visit savedVisit = visitRepository.findById(response.getId()).orElseThrow();
        assertThat(savedVisit.getStartDateTime()).isEqualTo(start);
        assertThat(savedVisit.getEndDateTime()).isEqualTo(end);
    }

    @Test
    void createVisit_ThrowsException_WhenPatientNotFound() {
        CreateVisitRequest request = new CreateVisitRequest();
        request.setPatientId(999); // Non-existent patient
        request.setDoctorId(doctor1.getId());
        request.setStart("2023-10-26T10:00:00Z");
        request.setEnd("2023-10-26T10:30:00Z");

        assertThatThrownBy(() -> visitTrackingService.createVisit(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found");
    }

    @Test
    void createVisit_ThrowsException_WhenDoctorNotFound() {
        CreateVisitRequest request = new CreateVisitRequest();
        request.setPatientId(patient1.getId());
        request.setDoctorId(999); // Non-existent doctor
        request.setStart("2023-10-26T10:00:00Z");
        request.setEnd("2023-10-26T10:30:00Z");

        assertThatThrownBy(() -> visitTrackingService.createVisit(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Doctor not found");
    }

    @Test
    void createVisit_ThrowsException_WhenEndTimeBeforeStartTime() {
        CreateVisitRequest request = new CreateVisitRequest();
        request.setPatientId(patient1.getId());
        request.setDoctorId(doctor1.getId());
        request.setStart("2023-10-26T10:30:00Z"); // End is before start
        request.setEnd("2023-10-26T10:00:00Z");

        assertThatThrownBy(() -> visitTrackingService.createVisit(request))
                .isInstanceOf(DateTimeNotValidException.class)
                .hasMessage("End time must be after start time");
    }

    @Test
    void createVisit_ThrowsException_WhenVisitConflicts() {
        // Создаем первый визит
        LocalDateTime start1 = LocalDateTime.of(2023, 10, 26, 10, 0, 0);
        LocalDateTime end1 = LocalDateTime.of(2023, 10, 26, 10, 30, 0);
        visitTrackingService.createVisit(createRequest(patient1.getId(), doctor1.getId(), start1, end1));

        // Попытка создать пересекающийся визит
        LocalDateTime start2 = LocalDateTime.of(2023, 10, 26, 10, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 10, 26, 10, 45, 0);
        CreateVisitRequest conflictingRequest = createRequest(patient2.getId(), doctor1.getId(), start2, end2);

        assertThatThrownBy(() -> visitTrackingService.createVisit(conflictingRequest))
                .isInstanceOf(ConflictResourceException.class)
                .hasMessage("Visit conflicts with existing visit");
    }

    @Test
    void getPatientVisits_ReturnsCorrectData() {
        // Подготовим данные
        LocalDateTime p1d1v1_start = LocalDateTime.of(2023, 10, 20, 9, 0, 0);
        LocalDateTime p1d1v1_end = LocalDateTime.of(2023, 10, 20, 9, 30, 0);
        visitTrackingService.createVisit(createRequest(patient1.getId(), doctor1.getId(), p1d1v1_start, p1d1v1_end));

        LocalDateTime p1d2v1_start = LocalDateTime.of(2023, 10, 21, 14, 0, 0);
        LocalDateTime p1d2v1_end = LocalDateTime.of(2023, 10, 21, 14, 45, 0);
        visitTrackingService.createVisit(createRequest(patient1.getId(), doctor2.getId(), p1d2v1_start, p1d2v1_end));

        LocalDateTime p2d1v1_start = LocalDateTime.of(2023, 10, 22, 11, 0, 0);
        LocalDateTime p2d1v1_end = LocalDateTime.of(2023, 10, 22, 11, 15, 0);
        visitTrackingService.createVisit(createRequest(patient2.getId(), doctor1.getId(), p2d1v1_start, p2d1v1_end));

        // Еще один визит для doctor1, чтобы убедиться в подсчете totalPatients
        LocalDateTime p2d1v2_start = LocalDateTime.of(2023, 10, 23, 13, 0, 0);
        LocalDateTime p2d1v2_end = LocalDateTime.of(2023, 10, 23, 13, 30, 0);
        visitTrackingService.createVisit(createRequest(patient2.getId(), doctor1.getId(), p2d1v2_start, p2d1v2_end));

        Pageable pageable = PageRequest.of(0, 10);
        PatientVisitsResponse response = visitTrackingService.getPatientVisits(pageable, null, null);

        assertThat(response).isNotNull();
        assertThat(response.getCount()).isEqualTo(3);
        assertThat(response.getData()).hasSize(3);

        // Проверяем данные
        PatientVisitDto aliceVisits = response.getData().stream()
                .filter(p -> p.getFirstName().equals("Alice") && p.getLastName().equals("Smith"))
                .findFirst()
                .orElse(null);
        assertThat(aliceVisits).isNotNull();
        assertThat(aliceVisits.getLastVisits()).hasSize(3);

        // Проверяем данные
        PatientVisitDto bobVisits = response.getData().stream()
                .filter(p -> p.getFirstName().equals("Bob") && p.getLastName().equals("Johnson"))
                .findFirst()
                .orElse(null);
        assertThat(bobVisits).isNotNull();
        assertThat(bobVisits.getLastVisits()).hasSize(3);

        // Проверяем детали визитов и подсчет пациентов у докторов

        LastVisitDto aliceVisitToDrEmily = aliceVisits.getLastVisits().stream()
                .filter(lv -> lv.getDoctor().getFirstName().equals("Dr. Emily"))
                .findFirst().orElse(null);
        assertThat(aliceVisitToDrEmily).isNotNull();
        assertThat(aliceVisitToDrEmily.getDoctor().getTotalPatients()).isEqualTo(2);

        LastVisitDto aliceVisitToDrJohn = aliceVisits.getLastVisits().stream()
                .filter(lv -> lv.getDoctor().getFirstName().equals("Dr. John"))
                .findFirst().orElse(null);
        assertThat(aliceVisitToDrJohn).isNotNull();
        assertThat(aliceVisitToDrJohn.getDoctor().getTotalPatients()).isEqualTo(2);
    }

    @Test
    void getPatientVisits_WithSearchFilter() {
        // Подготовим данные
        LocalDateTime p1d1v1_start = LocalDateTime.of(2023, 10, 20, 9, 0, 0);
        LocalDateTime p1d1v1_end = LocalDateTime.of(2023, 10, 20, 9, 30, 0);
        visitTrackingService.createVisit(createRequest(patient1.getId(), doctor1.getId(), p1d1v1_start, p1d1v1_end));

        LocalDateTime p2d1v1_start = LocalDateTime.of(2023, 10, 22, 11, 0, 0);
        LocalDateTime p2d1v1_end = LocalDateTime.of(2023, 10, 22, 11, 15, 0);
        visitTrackingService.createVisit(createRequest(patient2.getId(), doctor1.getId(), p2d1v1_start, p2d1v1_end));

        Pageable pageable = PageRequest.of(0, 10);
        PatientVisitsResponse response = visitTrackingService.getPatientVisits(pageable, "Ali", null);

        assertThat(response).isNotNull();
        assertThat(response.getCount()).isEqualTo(1); // Должен быть только Alice
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void getPatientVisits_WithDoctorIdsFilter() {
        // Подготовим данные
        LocalDateTime p1d1v1_start = LocalDateTime.of(2023, 10, 20, 9, 0, 0);
        LocalDateTime p1d1v1_end = LocalDateTime.of(2023, 10, 20, 9, 30, 0);
        visitTrackingService.createVisit(createRequest(patient1.getId(), doctor1.getId(), p1d1v1_start, p1d1v1_end));

        LocalDateTime p1d2v1_start = LocalDateTime.of(2023, 10, 21, 14, 0, 0);
        LocalDateTime p1d2v1_end = LocalDateTime.of(2023, 10, 21, 14, 45, 0);
        visitTrackingService.createVisit(createRequest(patient1.getId(), doctor2.getId(), p1d2v1_start, p1d2v1_end));

        LocalDateTime p2d1v1_start = LocalDateTime.of(2023, 10, 22, 11, 0, 0);
        LocalDateTime p2d1v1_end = LocalDateTime.of(2023, 10, 22, 11, 15, 0);
        visitTrackingService.createVisit(createRequest(patient2.getId(), doctor1.getId(), p2d1v1_start, p2d1v1_end));


        Pageable pageable = PageRequest.of(0, 10);
        List<Integer> doctorIds = Collections.singletonList(doctor2.getId()); // Фильтр по doctor2

        PatientVisitsResponse response = visitTrackingService.getPatientVisits(pageable, null, doctorIds);

        assertThat(response).isNotNull();
        assertThat(response.getCount()).isEqualTo(2); // Только Alice, у которой визит к doctor2
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData().get(0).getFirstName()).isEqualTo("Bob");
        assertThat(response.getData().get(0).getLastVisits()).hasSize(1);
        assertThat(response.getData().get(0).getLastVisits().get(0).getDoctor().getLastName()).isEqualTo("Doe");
    }

    // Вспомогательный метод для создания запросов
    private CreateVisitRequest createRequest(Integer patientId, Integer doctorId, LocalDateTime start, LocalDateTime end) {
        CreateVisitRequest request = new CreateVisitRequest();
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);

        // Получаем таймзону доктора
        Doctor doc = doctorRepository.findById(doctorId).orElseThrow();

        // Преобразуем локальное время в строку ISO_INSTANT (UTC) с учетом таймзоны доктора
        request.setStart(ZonedDateTime.of(start, java.time.ZoneId.of(doc.getTimezone()))
                .withZoneSameInstant(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT));
        request.setEnd(ZonedDateTime.of(end, java.time.ZoneId.of(doc.getTimezone()))
                .withZoneSameInstant(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT));
        return request;
    }
}
