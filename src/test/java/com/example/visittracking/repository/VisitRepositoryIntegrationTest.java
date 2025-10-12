package com.example.visittracking.repository;

import com.example.visittracking.TestBase;
import com.example.visittracking.entity.Doctor;
import com.example.visittracking.entity.Patient;
import com.example.visittracking.entity.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Pavel Zhurenkov
 */

@Transactional
class VisitRepositoryIntegrationTest extends TestBase{

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

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

    @Test
    void findOverlappingVisit_NoOverlap() {
        // Проверяем на отсутствие пересечений
        Optional<Visit> result = visitRepository.findOverlappingVisit(
                doctor1.getId(),
                LocalDateTime.of(2025, 9, 21, 11, 0), // После visit1
                LocalDateTime.of(2025, 9, 21, 12, 30)
        );
        assertThat(result).isEmpty();

        result = visitRepository.findOverlappingVisit(
                doctor1.getId(),
                LocalDateTime.of(2025, 9, 19, 9, 0), // До visit1
                LocalDateTime.of(2025, 9, 19, 9, 30)
        );
        assertThat(result).isEmpty();
    }

    @Test
    void findOverlappingVisit_PartialOverlap_StartBeforeExistingEnd() {
        // Новый визит: 10:45 - 11:45 (частичное пересечение в конце)
        Optional<Visit> result = visitRepository.findOverlappingVisit(
                doctor1.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 45),
                LocalDateTime.of(2025, 9, 20, 11, 45)
        );
        assertThat(result).isPresent();
    }

    @Test
    void findOverlappingVisit_PartialOverlap_EndAfterExistingStart() {
        // Новый визит: 9:45 - 10:15 (частичное пересечение в начале)
        Optional<Visit> result = visitRepository.findOverlappingVisit(
                doctor1.getId(),
                LocalDateTime.of(2025, 9, 20, 9, 45),
                LocalDateTime.of(2025, 9, 20, 10, 15)
        );
        assertThat(result).isPresent();
    }

    @Test
    void findOverlappingVisit_FullOverlap_NewVisitInsideExisting() {
        // Новый визит: 10:15 - 10:45 (полностью внутри)
        Optional<Visit> result = visitRepository.findOverlappingVisit(
                doctor1.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 15),
                LocalDateTime.of(2025, 9, 20, 10, 45)
        );
        assertThat(result).isPresent();
    }

    @Test
    void findOverlappingVisit_ExactOverlap() {
        // Новый визит: 10:00 - 10:30 (точно такой же)
        Optional<Visit> result = visitRepository.findOverlappingVisit(
                doctor1.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 0),
                LocalDateTime.of(2025, 9, 20, 11, 0)
        );
        assertThat(result).isPresent();
    }

    @Test
    void findPatientsByDoctorIds_ReturnsCorrectPatients() {
        // Визиты для doctor1
        Visit visit1_p1_d1 = new Visit(null, LocalDateTime.now(), LocalDateTime.now().plusHours(1), patient1, doctor1);
        Visit visit2_p2_d1 = new Visit(null, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), patient2, doctor1);
        visitRepository.save(visit1_p1_d1);
        visitRepository.save(visit2_p2_d1);

        // Визит для doctor2 (и patient1)
        Visit visit3_p1_d2 = new Visit(null, LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(5), patient1, doctor2);

        visitRepository.save(visit3_p1_d2);

        List<Integer> doctorIds = Collections.singletonList(doctor1.getId());
        List<Patient> patients = visitRepository.findPatientsByDoctorIds(doctorIds);

        assertThat(patients).hasSize(2);
        assertThat(patients).extracting(Patient::getFirstName)
                .containsExactlyInAnyOrder(patient1.getFirstName(), patient2.getFirstName());
    }

    @Test
    void findVisitsWithFilters_NoFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> visitPage = visitRepository.findVisitsWithFilters(null, null, pageable);

        assertThat(visitPage.getTotalElements()).isEqualTo(3);
        assertThat(visitPage.getContent()).hasSize(3);
        // Проверка сортировки по имени пациента
        assertThat(visitPage.getContent().get(0).getPatient().getLastName()).isEqualTo("Brown");
        assertThat(visitPage.getContent().get(1).getPatient().getLastName()).isEqualTo("Johnson");
        assertThat(visitPage.getContent().get(2).getPatient().getLastName()).isEqualTo("Smith");
    }

    @Test
    void findVisitsWithFilters_WithSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> visitPage = visitRepository.findVisitsWithFilters("Ali", null, pageable);

        assertThat(visitPage.getTotalElements()).isEqualTo(1);
        assertThat(visitPage.getContent()).hasSize(1);
        assertThat(visitPage.getContent().get(0).getPatient().getFirstName()).isEqualTo("Alice");
    }

    @Test
    void findVisitsWithFilters_WithDoctorIds() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Integer> doctorIds = Collections.singletonList(doctor1.getId());
        Page<Visit> visitPage = visitRepository.findVisitsWithFilters(null, doctorIds, pageable);

        assertThat(visitPage.getTotalElements()).isEqualTo(1);
        assertThat(visitPage.getContent()).hasSize(1);
        assertThat(visitPage.getContent().get(0).getDoctor().getId()).isEqualTo(doctor1.getId());
    }

    @Test
    void countPatientsWithFilters_NoFilters() {
        Long count = visitRepository.countPatientsWithFilters(null, null);
        assertThat(count).isEqualTo(3);
    }

    @Test
    void countPatientsWithFilters_WithSearch() {
        Long count = visitRepository.countPatientsWithFilters("john", null);
        assertThat(count).isEqualTo(1); // Только Bob Johnson
    }

    @Test
    void findVisitsByPatientId_ReturnsCorrectVisitsOrderedByTime() {
        // добавляем второй visit
        Visit visit2 = Visit.builder()
                .patient(patient1)
                .doctor(doctor2)
                .startDateTime(LocalDateTime.of(2025, 10, 4, 11, 0))
                .endDateTime(LocalDateTime.of(2025, 10, 4, 12, 0))
                .build();
        visitRepository.save(visit2);
        List<Visit> visits = visitRepository.findVisitsByPatientId(patient1.getId());

        assertThat(visits).hasSize(2);
        // Проверяем сортировку по убыванию времени начала
        assertThat(visits.get(0).getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 4, 11, 0));
        assertThat(visits.get(1).getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 9, 20, 10, 0));

    }

    @Test
    void countTotalPatientsByDoctorId_ReturnsCorrectCount() {
        // Визиты для doctor1:
        Visit visit1 = new Visit(null, LocalDateTime.of(2025,11,1,9,0),
                LocalDateTime.of(2025,11,1,9,30), patient1, doctor1);
        Visit visit2 = new Visit(null, LocalDateTime.of(2025,11,1,10,0),
                LocalDateTime.of(2025,11,1,10,30), patient2, doctor1);
        Visit visit3 = new Visit(null, LocalDateTime.of(2025,11,1,11,0),
                LocalDateTime.of(2025,11,1,11,30), patient1, doctor1);

        visitRepository.save(visit1);
        visitRepository.save(visit2);
        visitRepository.save(visit3);

        // Визиты для doctor2:
        Visit visit4 = new Visit(null, LocalDateTime.of(2023,11,1,12,0),
                LocalDateTime.of(2023,11,1,12,30), patient1, doctor2);

        visitRepository.save(visit4);


        Long countDoctor1 = visitRepository.countTotalPatientsByDoctorId(doctor1.getId());
        assertThat(countDoctor1).isEqualTo(2);

        Long countDoctor2 = visitRepository.countTotalPatientsByDoctorId(doctor2.getId());
        assertThat(countDoctor2).isEqualTo(2);
    }
}
