package com.my_nutri_calc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my_nutri_calc.model.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
    
}
