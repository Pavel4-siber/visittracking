package com.example.visittracking.repository;

import com.example.visittracking.TestBase;
import com.example.visittracking.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Pavel Zhurenkov
 */

class PatientRepositoryTest extends TestBase {

    @Autowired
    private PatientRepository patientRepository;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {
        patient1 = patientRepository.findById(1).get();

        patient2 = patientRepository.findById(2).get();
    }

    @Test
    void shouldSavePatient() {
        // Given
        Patient patient = Patient.builder()
                .firstName("Olaf")
                .lastName("Show")
                .build();
        // When
        Patient savePatient = patientRepository.save(patient);

        // Then
        assertThat(savePatient).isNotNull();
        assertThat(savePatient.getId()).isNotNull();
        assertThat(savePatient.getFirstName()).isEqualTo(patient.getFirstName());

    }

}
