package com.recipe.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JavaBean representing a culinary recipe in the system.
 */
public class Recipe implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String category;
    private String description;
    private List<String> ingredients = new ArrayList<>();
    private List<String> instructions = new ArrayList<>();
    private int preparationTime; // in minutes
    private int cookingTime;     // in minutes
    private int servings;
    private String difficulty;   // Easy, Medium, Hard
    private String imageUrl;

    public Recipe() {
    }

    public Recipe(String id, String name, String category, String description,
                  List<String> ingredients, List<String> instructions,
                  int preparationTime, int cookingTime, int servings,
                  String difficulty, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        if (ingredients != null) {
            this.ingredients = new ArrayList<>(ingredients);
        }
        if (instructions != null) {
            this.instructions = new ArrayList<>(instructions);
        }
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.servings = servings;
        this.difficulty = difficulty;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : "";
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category != null ? category.trim() : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients != null ? new ArrayList<>(ingredients) : new ArrayList<>();
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions != null ? new ArrayList<>(instructions) : new ArrayList<>();
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = Math.max(0, preparationTime);
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = Math.max(0, cookingTime);
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = Math.max(1, servings);
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty != null ? difficulty.trim() : "Easy";
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl != null ? imageUrl.trim() : "";
    }

    /**
     * Total preparation + cooking time in minutes.
     */
    public int getTotalTime() {
        return preparationTime + cookingTime;
    }

    /**
     * Formats total time nicely (e.g. "45 mins" or "1 hr 15 mins").
     */
    public String getFormattedTotalTime() {
        int total = getTotalTime();
        if (total <= 0) return "Quick";
        if (total < 60) {
            return total + " mins";
        }
        int hours = total / 60;
        int mins = total % 60;
        return mins > 0 ? hours + " hr " + mins + " mins" : hours + " hr";
    }

    /**
     * Formats preparation time for display.
     */
    public String getFormattedPrepTime() {
        return preparationTime > 0 ? preparationTime + " mins" : "Minimal";
    }

    /**
     * Formats cooking time for display.
     */
    public String getFormattedCookTime() {
        return cookingTime > 0 ? cookingTime + " mins" : "No cook";
    }

    /**
     * Converts ingredients list to newline-separated string for form textarea.
     */
    public String getIngredientsAsText() {
        if (ingredients == null || ingredients.isEmpty()) return "";
        return String.join("\n", ingredients);
    }

    /**
     * Converts instructions list to newline-separated string for form textarea.
     */
    public String getInstructionsAsText() {
        if (instructions == null || instructions.isEmpty()) return "";
        return String.join("\n", instructions);
    }

    /**
     * Returns a safe image URL or a category-based fallback illustration.
     */
    public String getDisplayImageUrl() {
        if (imageUrl != null && !imageUrl.isBlank()) {
            return imageUrl;
        }
        // Fallback Unsplash food pictures by category
        if (category == null) return "https://images.unsplash.com/photo-1495521821757-a1efb6729352?auto=format&fit=crop&w=800&q=80";
        return switch (category.toLowerCase()) {
            case "breakfast" -> "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=800&q=80";
            case "lunch" -> "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=800&q=80";
            case "dinner" -> "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=800&q=80";
            case "desserts" -> "https://images.unsplash.com/photo-1551024709-8f23befc6f87?auto=format&fit=crop&w=800&q=80";
            case "snacks" -> "https://images.unsplash.com/photo-1599490659213-e2b9527bd087?auto=format&fit=crop&w=800&q=80";
            case "beverages" -> "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=800&q=80";
            default -> "https://images.unsplash.com/photo-1495521821757-a1efb6729352?auto=format&fit=crop&w=800&q=80";
        };
    }

    /**
     * Validates recipe model fields and returns a list of error descriptions.
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            errors.add("Recipe name is required.");
        } else if (name.length() > 100) {
            errors.add("Recipe name cannot exceed 100 characters.");
        }

        if (category == null || category.trim().isEmpty()) {
            errors.add("Category is required.");
        }

        if (description == null || description.trim().isEmpty()) {
            errors.add("Short description is required.");
        } else if (description.length() > 500) {
            errors.add("Description cannot exceed 500 characters.");
        }

        if (ingredients == null || ingredients.isEmpty()) {
            errors.add("At least one ingredient is required.");
        }

        if (instructions == null || instructions.isEmpty()) {
            errors.add("At least one preparation step is required.");
        }

        if (preparationTime < 0) {
            errors.add("Preparation time cannot be negative.");
        }

        if (cookingTime < 0) {
            errors.add("Cooking time cannot be negative.");
        }

        if (servings < 1) {
            errors.add("Servings must be at least 1.");
        }

        if (difficulty == null || (!difficulty.equalsIgnoreCase("Easy") &&
                                   !difficulty.equalsIgnoreCase("Medium") &&
                                   !difficulty.equalsIgnoreCase("Hard"))) {
            errors.add("Difficulty must be Easy, Medium, or Hard.");
        }

        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe recipe)) return false;
        return Objects.equals(id, recipe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", totalTime=" + getTotalTime() +
                '}';
    }
}
