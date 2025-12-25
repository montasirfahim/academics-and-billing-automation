package com.example.repository;

import com.example.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Semester findSemesterBySemesterId(Long semesterId);
    List<Semester> findAllByOrderBySemesterIdDesc();

    Semester findByCustomSemesterCode(String customSemesterCode);

}
