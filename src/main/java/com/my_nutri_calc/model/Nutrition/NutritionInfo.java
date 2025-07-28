package com.my_nutri_calc.model.Nutrition;

import jakarta.persistence.Embeddable;

@Embeddable
public class NutritionInfo {

    private double proteins;
    private double carbs;
    private double fats;
    private double calories;

    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public NutritionInfo(double proteins, double carbs, double fats, double calories) {
        this.proteins = proteins;
        this.carbs = carbs;
        this.fats = fats;
        this.calories = calories;
    }

    public NutritionInfo(double proteins, double carbs, double fats) {
        this(proteins, carbs, fats, 0.0);
    }

    public NutritionInfo() {}

}
