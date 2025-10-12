package com.example.visittracking.repository;

import com.example.visittracking.TestBase;
import com.example.visittracking.entity.Doctor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Pavel Zhurenkov
 */

class DoctorRepositoryTest extends TestBase {

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    @DisplayName("Should save a doctor successfully")
    void shouldSaveDoctor() {
        // Given
                Doctor doctor = Doctor.builder().firstName("Dr. Karl")
                .lastName("Jonson")
                .timezone("Pacific/Apia")
                .build();
        // When
        Doctor savedDoctor = doctorRepository.save(doctor);

        // Then
        assertThat(savedDoctor).isNotNull();
        assertThat(savedDoctor.getId()).isNotNull();
        assertThat(savedDoctor.getFirstName()).isEqualTo("Dr. Karl");

    }

    @Test
    @DisplayName("Should find a doctor by ID")
    void shouldFindDoctorById() {
        // Given
        Doctor doctor = Doctor.builder()
                .firstName("Dr. Jane Smith")
                .lastName("Verde")
                .timezone("America/Noronha")
                .build();
        doctorRepository.save(doctor); // Сохраняем доктора, чтобы его можно было найти

        // When
        Optional<Doctor> foundDoctorOptional = doctorRepository.findById(doctor.getId());

        // Then
        assertThat(foundDoctorOptional).isPresent();
        assertThat(foundDoctorOptional.get().getFirstName()).isEqualTo("Dr. Jane Smith");
    }

    @Test
    @DisplayName("Should return empty optional when doctor not found by ID")
    void shouldReturnEmptyOptionalWhenDoctorNotFound() {
        // Given
        Integer nonExistentId = 999;

        // When
        Optional<Doctor> foundDoctorOptional = doctorRepository.findById(nonExistentId);

        // Then
        assertThat(foundDoctorOptional).isNotPresent();
    }

    @Test
    @DisplayName("Should delete a doctor by ID")
    void shouldDeleteDoctorById() {
        // Given
        Doctor doctor = Doctor.builder()
                .firstName("Dr. Dre")
                .lastName("Kos")
                .timezone("Atlantic/South_Georgia")
                .build();
        doctorRepository.save(doctor);
        Integer doctorId = doctor.getId();

        // When
        doctorRepository.deleteById(doctorId);

        // Then
        Optional<Doctor> deletedDoctor = doctorRepository.findById(doctorId);
        assertThat(deletedDoctor).isNotPresent();
    }

    @Test
    @DisplayName("Should find all doctors")
    void shouldFindAllDoctors() {
        // Given
        // When
        Iterable<Doctor> doctors = doctorRepository.findAll();

        // Then
        assertThat(doctors).hasSize(5);
    }
}
