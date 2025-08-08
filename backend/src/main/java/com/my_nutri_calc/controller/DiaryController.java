package com.my_nutri_calc.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.my_nutri_calc.model.DiaryEntry;
import com.my_nutri_calc.model.Meal;
import com.my_nutri_calc.model.Nutrition.NutritionCalculator;
import com.my_nutri_calc.model.User;
import com.my_nutri_calc.repository.DiaryRepository;
import com.my_nutri_calc.repository.MealRepository;
import com.my_nutri_calc.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/diary")
@RequiredArgsConstructor
public class DiaryController {
    
    private final DiaryRepository diaryRepository;
    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> createDiaryEntry(
            @RequestBody DiaryEntry diaryEntry,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    
        diaryEntry.setUser(user);
    
        List<Meal> linkedMeals = new ArrayList<>();
        for (Meal meal : diaryEntry.getMeals()) {
            Meal m = mealRepository.findById(meal.getId())
                    .orElseThrow(() -> new RuntimeException("Meal not found with id: " + meal.getId()));
            linkedMeals.add(m);
        }
    
        diaryEntry.setMeals(linkedMeals);
        diaryEntry.setTotalNutrition(NutritionCalculator.calculateDiaryTotal(linkedMeals));
    
        DiaryEntry saved = diaryRepository.save(diaryEntry);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> updateDiaryEntry(
            @PathVariable Long id,
            @RequestBody DiaryEntry updatedEntry,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails
    ) {
        DiaryEntry existing = diaryRepository.findById(id)
                .orElse(null);
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diary entry not found");

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (!existing.getUser().getId().equals(user.getId()) && !user.getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<Meal> updatedMeals = new ArrayList<>();
        for (Meal meal : updatedEntry.getMeals()) {
            Meal m = mealRepository.findById(meal.getId())
                    .orElseThrow(() -> new RuntimeException("Meal not found with id: " + meal.getId()));
            updatedMeals.add(m);
        }

        existing.setDate(updatedEntry.getDate());
        existing.setMeals(updatedMeals);
        existing.setTotalNutrition(NutritionCalculator.calculateDiaryTotal(updatedMeals));

        DiaryEntry saved = diaryRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteDiaryEntry(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails
    ) {
        DiaryEntry entry = diaryRepository.findById(id).orElse(null);
        if (entry == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diary entry not found");

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (!entry.getUser().getId().equals(user.getId()) && !user.getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        diaryRepository.delete(entry);

        return ResponseEntity.ok("Deleted successfully");
    }

    @GetMapping("/all/{userId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllDiaryEntries(
            @PathVariable UUID userId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails
    ) {
        User requester = userRepository.findByUsername(userDetails.getUsername());
        if (!requester.getId().equals(userId) && !requester.getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<DiaryEntry> entries = diaryRepository.findByUserId(userId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getDiaryEntryById(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails
    ) {
        DiaryEntry entry = diaryRepository.findById(id).orElse(null);
        if (entry == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diary entry not found");

        User requester = userRepository.findByUsername(userDetails.getUsername());
        if (!entry.getUser().getId().equals(requester.getId()) && !requester.getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        return ResponseEntity.ok(entry);
    }

    @GetMapping("/by-date")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getDiaryEntriesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        List<DiaryEntry> diaryEntries = diaryRepository.findByUserAndDate(user, date);

        return ResponseEntity.ok(diaryEntries);
    }

}
