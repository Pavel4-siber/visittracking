package com.example.visittracking.repository;

import com.example.visittracking.entity.Patient;
import com.example.visittracking.entity.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Pavel Zhurenkov
 */
@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

    @Query("SELECT v FROM Visit v " +
            "WHERE v.doctor.id = :doctorId " +
            "AND (:startDateTime BETWEEN v.startDateTime AND v.endDateTime " +
            "OR :endDateTime BETWEEN v.startDateTime AND v.endDateTime " +
            "OR v.startDateTime BETWEEN :startDateTime AND :endDateTime)")
    Optional<Visit> findOverlappingVisit(
            @Param("doctorId") Integer doctorId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT DISTINCT v.patient FROM Visit v WHERE v.doctor.id IN :doctorIds")
    List<Patient> findPatientsByDoctorIds(@Param("doctorIds") List<Integer> doctorIds);

    @Query("SELECT v FROM Visit v " +
            "WHERE (:search IS NULL OR " +
            "(v.patient.firstName LIKE %:search% OR v.patient.lastName LIKE %:search%)) " +
            "AND (:doctorIds IS NULL OR v.doctor.id IN :doctorIds) " +
            "ORDER BY v.patient.lastName, v.patient.firstName")
    Page<Visit> findVisitsWithFilters(
            @Param("search") String search,
            @Param("doctorIds") List<Integer> doctorIds,
            Pageable pageable);

    @Query("SELECT COUNT(DISTINCT v.patient) FROM Visit v " +
            "WHERE (:search IS NULL OR " +
            "(v.patient.firstName LIKE %:search% OR v.patient.lastName LIKE %:search%)) " +
            "AND (:doctorIds IS NULL OR v.doctor.id IN :doctorIds)")
    Long countPatientsWithFilters(
            @Param("search") String search,
            @Param("doctorIds") List<Integer> doctorIds);

    @Query("SELECT v FROM Visit v WHERE v.patient.id = :patientId ORDER BY v.startDateTime DESC")
    List<Visit> findVisitsByPatientId(@Param("patientId") Integer patientId);

    @Query("SELECT COUNT(DISTINCT v.patient) FROM Visit v WHERE v.doctor.id = :doctorId")
    Long countTotalPatientsByDoctorId(@Param("doctorId") Integer doctorId);
}
