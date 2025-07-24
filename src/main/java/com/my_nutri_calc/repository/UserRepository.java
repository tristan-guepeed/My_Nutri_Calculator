package com.my_nutri_calc.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my_nutri_calc.model.User;

public interface  UserRepository extends JpaRepository<User, UUID> {
    
}
