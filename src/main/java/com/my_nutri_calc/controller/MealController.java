package com.my_nutri_calc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.my_nutri_calc.model.Meal;
import com.my_nutri_calc.model.MealItem;
import com.my_nutri_calc.model.Nutrition.NutritionCalculator;
import com.my_nutri_calc.model.Nutrition.NutritionInfo;
import com.my_nutri_calc.model.User;
import com.my_nutri_calc.repository.FoodRepository;
import com.my_nutri_calc.repository.MealRepository;
import com.my_nutri_calc.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/meals")
@RequiredArgsConstructor
public class MealController {
    
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    @Value("${app.admin.uuid}")
    private String adminUuidString;

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @GetMapping("/visible/{userId}")
    public ResponseEntity<?> getUserMealsWithAdmin(
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
            return ResponseEntity.ok(mealRepository.findByCreatedById(adminUuid));
        }

        List<Meal> userMeals = mealRepository.findByCreatedById(userId);
        List<Meal> adminMeals = mealRepository.findByCreatedById(adminUuid);

        List<Meal> combined = new ArrayList<>();
        combined.addAll(adminMeals);
        combined.addAll(userMeals);

        return ResponseEntity.ok(combined);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> createMealWithItems(
            @RequestBody Meal meal,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
            
        if (mealRepository.findByName(meal.getName()) != null) {
            return ResponseEntity.badRequest().body("Name already exists");
        }
    
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    
        meal.setCreatedBy(user);
    
        for (MealItem item : meal.getMealItems()) {
            Food food = foodRepository.findById(item.getFood().getId())
                    .orElseThrow(() -> new RuntimeException("Food not found with id " + item.getFood().getId()));
            item.setFood(food);
            item.setMeal(meal);
        }
    
        NutritionInfo totalNutrition = NutritionCalculator.calculateTotal(meal.getMealItems());

        meal.setTotalNutrition(totalNutrition);

        Meal savedMeal = mealRepository.save(meal);
        return ResponseEntity.ok(savedMeal);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> updateMeal(
            @PathVariable Long id,
            @RequestBody Meal mealUpdate,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
            
        Meal existingMeal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));
            
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    
        existingMeal.setName(mealUpdate.getName());
        existingMeal.setCreatedBy(user);
    
        Map<Long, MealItem> existingItemsMap = existingMeal.getMealItems().stream()
                .collect(Collectors.toMap(MealItem::getId, Function.identity()));
    
        List<MealItem> updatedItems = new ArrayList<>();
    
        for (MealItem itemUpdate : mealUpdate.getMealItems()) {
            if (itemUpdate.getId() != null && existingItemsMap.containsKey(itemUpdate.getId())) {
                MealItem existingItem = existingItemsMap.get(itemUpdate.getId());
            
                Food food = foodRepository.findById(itemUpdate.getFood().getId())
                        .orElseThrow(() -> new RuntimeException("Food not found with id " + itemUpdate.getFood().getId()));
            
                existingItem.setFood(food);
                existingItem.setQuantity(itemUpdate.getQuantity());
            
                updatedItems.add(existingItem);
            
                existingItemsMap.remove(itemUpdate.getId());
            
            } else {
                Food food = foodRepository.findById(itemUpdate.getFood().getId())
                        .orElseThrow(() -> new RuntimeException("Food not found with id " + itemUpdate.getFood().getId()));
            
                MealItem newItem = new MealItem();
                newItem.setFood(food);
                newItem.setQuantity(itemUpdate.getQuantity());
                newItem.setMeal(existingMeal);
            
                updatedItems.add(newItem);
            }
        }
    
        for (MealItem itemToDelete : existingItemsMap.values()) {
            existingMeal.getMealItems().remove(itemToDelete);
        }
    
        existingMeal.getMealItems().clear();
        existingMeal.getMealItems().addAll(updatedItems);

        NutritionInfo totalNutrition = NutritionCalculator.calculateTotal(existingMeal.getMealItems());

        existingMeal.setTotalNutrition(totalNutrition);
    
        Meal savedMeal = mealRepository.save(existingMeal);
    
        return ResponseEntity.ok(savedMeal);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMeal(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        Meal meal = mealRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Meal not found"));

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        if (!user.getRole().equals("ADMIN") && !meal.getCreatedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this meal");
        }

        mealRepository.delete(meal);
        return ResponseEntity.ok("Meal deleted successfully");
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadMealImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        try {
            Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

            User user = userRepository.findByUsername(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            if (!user.getRole().equals("ADMIN") && !meal.getCreatedBy().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this meal image");
            }

            meal.setImageData(file.getBytes());
            meal.setImageType(file.getContentType());

            mealRepository.save(meal);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<String> deleteMealImage(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        Meal meal = mealRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Meal not found"));

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        if (!user.getRole().equals("ADMIN") && !meal.getCreatedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this meal image");
        }

        meal.setImageData(null);
        meal.setImageType(null);

        mealRepository.save(meal);

        return ResponseEntity.ok("Image deleted successfully");
    }

}
