package com.my_nutri_calc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my_nutri_calc.model.Food;
import com.my_nutri_calc.model.Nutrition.NutritionInfo;
import com.my_nutri_calc.model.User;
import com.my_nutri_calc.repository.FoodRepository;
import com.my_nutri_calc.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/foods")
@RequiredArgsConstructor
public class FoodController {
    
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Value("${app.admin.uuid}")
    private String adminUuidString;

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @GetMapping("/visible/{userId}")
    public ResponseEntity<?> getUserFoodsWithAdmin(
            @PathVariable UUID userId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails
    ) {
        String usernameFromToken = userDetails.getUsername();

        User userFromToken = userRepository.findByUsername(usernameFromToken);
        if (userFromToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    
        if (!userFromToken.getId().equals(userId) && !userFromToken.getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this data");
        }
    
        UUID adminUuid = UUID.fromString(adminUuidString);

        if (userId.equals(adminUuid)) {
            return ResponseEntity.ok(foodRepository.findByCreatedById(adminUuid));
        }

        List<Food> userFoods = foodRepository.findByCreatedById(userId);
        List<Food> adminFoods = foodRepository.findByCreatedById(adminUuid);

        List<Food> combined = new ArrayList<>();
        combined.addAll(adminFoods);
        combined.addAll(userFoods);
    
        return ResponseEntity.ok(combined);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/create")
    public ResponseEntity<?> createFood(
            @RequestBody Food food,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        if (foodRepository.findByName(food.getName()) != null) {
            return ResponseEntity.badRequest().body("Name already exists");
        }

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        food.setCreatedBy(user);

        return ResponseEntity.ok(foodRepository.save(food));
    }


    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long id,
            @RequestBody Food food,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        Food existingFood = foodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Food not found"));

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        if (!user.getRole().equals("ADMIN") && !existingFood.getCreatedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this food");
        }

        if (food.getName() != null && !food.getName().isEmpty()) {
            existingFood.setName(food.getName());
        }

        if (food.getNi() != null) {
            NutritionInfo existingNi = existingFood.getNi();
            NutritionInfo newNi = food.getNi();

            if (newNi.getProteins() != 0) {
                existingNi.setProteins(newNi.getProteins());
            }
            if (newNi.getCarbs() != 0) {
                existingNi.setCarbs(newNi.getCarbs());
            }
            if (newNi.getFats() != 0) {
                existingNi.setFats(newNi.getFats());
            }
            if (newNi.getCalories() != 0) {
                existingNi.setCalories(newNi.getCalories());
            }

            existingFood.setNi(existingNi);
        }

        return ResponseEntity.ok(foodRepository.save(existingFood));
    }



    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFood(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        Food food = foodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Food not found"));

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        if (!user.getRole().equals("ADMIN") && !food.getCreatedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this food");
        }

        foodRepository.delete(food);
        return ResponseEntity.ok("Food deleted successfully");
    }


    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadFoodImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        try {
            Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));

            User user = userRepository.findByUsername(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            if (!user.getRole().equals("ADMIN") && !food.getCreatedBy().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this food image");
            }

            food.setImageData(file.getBytes());
            food.setImageType(file.getContentType());

            foodRepository.save(food);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }


    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<String> deleteFoodImage(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        Food food = foodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Food not found"));

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        if (!user.getRole().equals("ADMIN") && !food.getCreatedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this food image");
        }

        food.setImageData(null);
        food.setImageType(null);

        foodRepository.save(food);

        return ResponseEntity.ok("Image deleted successfully");
    }

}
