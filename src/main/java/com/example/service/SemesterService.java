package com.example.service;

import com.example.entity.Semester;
import com.example.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SemesterService {
    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private UtilityService utilityService;

    public void saveSemester(Semester semester) {
        String customCode = utilityService.generateCustomSemesterCode(semester.getSemesterParity(), semester.getSemesterScheduledYear());
        semester.setCustomSemesterCode(customCode);
        semesterRepository.save(semester);
    }

    public List<Semester> findAllSemesters() {
        return semesterRepository.findAllByOrderBySemesterIdDesc();
    }

    public void deleteBySemesterId(Long semesterId) {
        semesterRepository.deleteById(semesterId);
    }

    public Optional<Semester> findSemesterById(Long semesterId) {
        return Optional.ofNullable(semesterRepository.findSemesterBySemesterId(semesterId));
    }

    public Semester findById(Long semesterId) {
        return semesterRepository.findSemesterBySemesterId(semesterId);
    }

    public Semester findByCustomSemesterCode(String customSemesterCode) {
        return semesterRepository.findByCustomSemesterCode(customSemesterCode);
    }
}
