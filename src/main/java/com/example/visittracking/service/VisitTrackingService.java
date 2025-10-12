package com.example.visittracking.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Pavel Zhurenkov
 */
@Service
@Transactional(readOnly = true)
public class VisitTrackingService {

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    private final VisitRepository visitRepository;

    public VisitTrackingService(PatientRepository patientRepository, DoctorRepository doctorRepository,
                                VisitRepository visitRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional
    public VisitResponse createVisit(CreateVisitRequest request) {
        // Валидация входных данных
        if (request.getPatientId() == null || request.getDoctorId() == null) {
            throw new IllegalArgumentException("Patient ID and Doctor ID are required");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found", HttpStatus.NOT_FOUND.value()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found",HttpStatus.NOT_FOUND.value()));

        // Парсинг дат
        LocalDateTime startDateTime = parseDateTime(request.getStart(), doctor.getTimezone());
        LocalDateTime endDateTime = parseDateTime(request.getEnd(), doctor.getTimezone());

        if (startDateTime.isAfter(endDateTime)) {
            throw new DateTimeNotValidException("End time must be after start time", HttpStatus.BAD_REQUEST.value());
        }

        // Проверка пересечения времени
        Optional<Visit> existingVisit = visitRepository.findOverlappingVisit(
                doctor.getId(), startDateTime, endDateTime);

        if (existingVisit.isPresent()) {
            throw new ConflictResourceException("Visit conflicts with existing visit", HttpStatus.CONFLICT.value());
        }

        Visit visit = new Visit();
        visit.setStartDateTime(startDateTime);
        visit.setEndDateTime(endDateTime);
        visit.setPatient(patient);
        visit.setDoctor(doctor);

        Visit savedVisit = visitRepository.save(visit);

        return convertToResponse(savedVisit);
    }

    public PatientVisitsResponse getPatientVisits(Pageable pageable, String search, List<Integer> doctorIds) {
        // Получаем общее количество пациентов с учетом фильтров
        Long totalPatients = visitRepository.countPatientsWithFilters(search, doctorIds);

        // Получаем страницу визитов
        Page<Visit> visitPage = visitRepository.findVisitsWithFilters(search, doctorIds, pageable);

        // Группируем по пациентам и врачам для формирования результата
        Map<Integer, PatientVisitDto> patientMap = new HashMap<>();

        // Собираем уникальные ID врачей
        Set<Integer> doctorIdSet = visitPage.getContent().stream()
                .map(v -> v.getDoctor().getId())
                .collect(Collectors.toSet());

        // Получаем информацию о врачах с количеством пациентов
        Map<Integer, Long> doctorPatientCounts = new HashMap<>();
        for (Integer docId : doctorIdSet) {
            doctorPatientCounts.put(docId, visitRepository.countTotalPatientsByDoctorId(docId));
        }

        // Обрабатываем визиты
        for (Visit visit : visitPage.getContent()) {
            Integer patientId = visit.getPatient().getId();

            if (!patientMap.containsKey(patientId)) {
                PatientVisitDto patientDto = new PatientVisitDto();
                patientDto.setFirstName(visit.getPatient().getFirstName());
                patientDto.setLastName(visit.getPatient().getLastName());
                patientDto.setLastVisits(new ArrayList<>());
                patientMap.put(patientId, patientDto);
            }

            PatientVisitDto patientDto = patientMap.get(patientId);

            LastVisitDto lastVisitDto = new LastVisitDto();
            lastVisitDto.setStart(visit.getStartDateTime().toString());
            lastVisitDto.setEnd(visit.getEndDateTime().toString());

            DoctorInfoDto doctorInfo = new DoctorInfoDto();
            doctorInfo.setFirstName(visit.getDoctor().getFirstName());
            doctorInfo.setLastName(visit.getDoctor().getLastName());
            doctorInfo.setTotalPatients(doctorPatientCounts.get(visit.getDoctor().getId()));

            lastVisitDto.setDoctor(doctorInfo);
            patientDto.getLastVisits().add(lastVisitDto);
        }

        // Сортируем по имени пациента
        List<PatientVisitDto> result = patientMap.values().stream()
                .sorted(Comparator.comparing(PatientVisitDto::getLastName)
                        .thenComparing(PatientVisitDto::getFirstName))
                .collect(Collectors.toList());

        PatientVisitsResponse response = new PatientVisitsResponse();
        response.setData(result);
        response.setCount(totalPatients);

        return response;
    }

    private LocalDateTime parseDateTime(String dateTimeStr, String timezone) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeStr);
        ZonedDateTime convertedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of(timezone));
        return convertedDateTime.toLocalDateTime();
    }

    private VisitResponse convertToResponse(Visit visit) {
        VisitResponse response = new VisitResponse();
        response.setId(visit.getId());
        response.setStart(visit.getStartDateTime().toString());
        response.setEnd(visit.getEndDateTime().toString());
        response.setPatientId(visit.getPatient().getId());
        response.setDoctorId(visit.getDoctor().getId());
        return response;
    }
}
