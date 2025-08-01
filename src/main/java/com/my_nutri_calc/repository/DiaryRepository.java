package com.my_nutri_calc.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my_nutri_calc.model.DiaryEntry;
import com.my_nutri_calc.model.User;

public interface DiaryRepository extends JpaRepository<DiaryEntry, Long> {
    
    List<DiaryEntry> findByUserId(UUID userId);
    List<DiaryEntry> findByUserAndDate(User user, LocalDate date);
}
