package com.my_nutri_calc.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my_nutri_calc.model.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {

    Food findByName(String name);
    List<Food> findByCreatedById(UUID userId);

}
