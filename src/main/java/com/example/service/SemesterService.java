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

    public void saveSemester(Semester semester) {
        semesterRepository.save(semester);
    }

    public List<Semester> findAllSemesters() {
        return semesterRepository.findAll();
    }

    public void deleteBySemesterId(Long semesterId) {
        semesterRepository.deleteById(semesterId);
    }

    public Optional<Semester> findSemesterById(Long semesterId) {
        return Optional.ofNullable(semesterRepository.findSemesterBySemesterId(semesterId));
    }
}
