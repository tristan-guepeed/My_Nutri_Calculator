package com.my_nutri_calc.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my_nutri_calc.model.Food;
import com.my_nutri_calc.repository.FoodRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/food")
@RequiredArgsConstructor
public class FoodController {
    
    private final FoodRepository foodRepository;

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadFoodImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        try {
            Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));

            food.setImageData(file.getBytes());
            food.setImageType(file.getContentType());

            foodRepository.save(food);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<String> deleteFoodImage(@PathVariable Long id) {
        Food food = foodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Food not found"));

        food.setImageData(null);
        food.setImageType(null);

        foodRepository.save(food);

        return ResponseEntity.ok("Image deleted successfully");
    }


}
