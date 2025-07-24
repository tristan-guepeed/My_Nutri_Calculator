package com.my_nutri_calc.model.Nutrition;

import java.util.List;

import com.my_nutri_calc.model.Meal;
import com.my_nutri_calc.model.MealItem;

public class NutritionCalculator {

    public static NutritionInfo calculateTotal(List<MealItem> mealItems) {
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFats = 0;
        double totalCalories = 0;

        if (mealItems == null || mealItems.isEmpty()) {
            return new NutritionInfo(0, 0, 0, 0);
        }

        for (MealItem item : mealItems) {
            NutritionInfo ni = item.getFood().getNi();
            double quantityFactor = item.getQuantity() / 100.0;

            totalProtein += ni.getProteins() * quantityFactor;
            totalCarbs += ni.getCarbs() * quantityFactor;
            totalFats += ni.getFats() * quantityFactor;
            totalCalories += (ni.getProteins() * 4 + ni.getCarbs() * 4 + ni.getFats() * 9) * quantityFactor;
        }

        return new NutritionInfo(totalProtein, totalCarbs, totalFats, totalCalories);
    }

    public NutritionInfo calculateDiaryTotal(List<Meal> meals) {
        double totalProteins = 0;
        double totalCarbs = 0;
        double totalFats = 0;
        double totalCalories = 0;

        for (Meal meal : meals) {
            NutritionInfo mealNutrition = calculateTotal(meal.getMealItems());
            totalProteins += mealNutrition.getProteins();
            totalCarbs += mealNutrition.getCarbs();
            totalFats += mealNutrition.getFats();
            totalCalories += mealNutrition.getCalories();
        }

        return new NutritionInfo(totalProteins, totalCarbs, totalFats, totalCalories);
    }
}
