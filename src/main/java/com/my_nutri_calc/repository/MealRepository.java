package com.my_nutri_calc.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my_nutri_calc.model.Meal;

public interface MealRepository extends JpaRepository<Meal, Long> {
    Meal findByName(String name);
    List<Meal> findByCreatedById(UUID userId);
}
