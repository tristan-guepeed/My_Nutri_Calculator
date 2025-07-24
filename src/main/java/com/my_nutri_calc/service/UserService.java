package com.my_nutri_calc.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.my_nutri_calc.model.User;
import com.my_nutri_calc.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
