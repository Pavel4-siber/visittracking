package com.example.visittracking.repository;

import com.example.visittracking.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Pavel Zhurenkov
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
}
