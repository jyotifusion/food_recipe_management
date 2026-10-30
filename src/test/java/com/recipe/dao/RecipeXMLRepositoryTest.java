package com.recipe.dao;

import com.recipe.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeXMLRepositoryTest {

    @TempDir
    Path tempDir;

    private RecipeXMLRepository repository;
    private File testXmlFile;

    @BeforeEach
    void setUp() {
        testXmlFile = tempDir.resolve("test-recipes.xml").toFile();
        RecipeXMLRepository.initialize(testXmlFile.getAbsolutePath());
        repository = RecipeXMLRepository.getInstance();
    }

    @Test
    void testInitialSeedLoading() {
        List<Recipe> recipes = repository.getAllRecipes();
        assertNotNull(recipes);
        assertFalse(recipes.isEmpty(), "Repository should be seeded with sample recipes");
        assertTrue(recipes.size() >= 5, "Should have at least 5 sample recipes");
    }

    @Test
    void testFindById() {
        Optional<Recipe> opt = repository.findById("1");
        assertTrue(opt.isPresent(), "Recipe with ID 1 should exist");
        Recipe r = opt.get();
        assertEquals("Vegetable Pasta", r.getName());
        assertEquals("Dinner", r.getCategory());
        assertFalse(r.getIngredients().isEmpty());
        assertFalse(r.getInstructions().isEmpty());
    }

    @Test
    void testAddRecipe() throws IOException {
        int initialCount = repository.getTotalCount();
        Recipe newRecipe = new Recipe(
                null,
                "Avocado Toast",
                "Breakfast",
                "Toasted artisanal sourdough with mashed avocado and sea salt.",
                List.of("Sourdough bread", "Ripe avocado", "Olive oil", "Chili flakes"),
                List.of("Toast the bread until golden.", "Mash avocado with olive oil and salt.", "Spread over toast."),
                5,
                5,
                1,
                "Easy",
                "https://images.unsplash.com/photo-1588137378633-dea1336ce1e2"
        );

        Recipe saved = repository.addRecipe(newRecipe);
        assertNotNull(saved.getId(), "Generated ID should not be null");
        assertEquals(initialCount + 1, repository.getTotalCount());

        Optional<Recipe> fetched = repository.findById(saved.getId());
        assertTrue(fetched.isPresent());
        assertEquals("Avocado Toast", fetched.get().getName());
    }

    @Test
    void testUpdateRecipe() throws IOException {
        Optional<Recipe> opt = repository.findById("1");
        assertTrue(opt.isPresent());
        Recipe r = opt.get();

        r.setName("Updated Creamy Vegetable Pasta");
        r.setCookingTime(22);
        boolean updated = repository.updateRecipe(r);
        assertTrue(updated);

        Recipe reloaded = repository.findById("1").orElseThrow();
        assertEquals("Updated Creamy Vegetable Pasta", reloaded.getName());
        assertEquals(22, reloaded.getCookingTime());
    }

    @Test
    void testDeleteRecipe() throws IOException {
        int initialCount = repository.getTotalCount();
        assertTrue(repository.findById("2").isPresent());

        boolean deleted = repository.deleteRecipe("2");
        assertTrue(deleted);
        assertEquals(initialCount - 1, repository.getTotalCount());
        assertFalse(repository.findById("2").isPresent());
    }

    @Test
    void testSearchAndFilter() {
        List<Recipe> dinnerOnly = repository.searchAndFilter("", "Dinner", "", "name-asc");
        assertFalse(dinnerOnly.isEmpty());
        for (Recipe r : dinnerOnly) {
            assertEquals("Dinner", r.getCategory());
        }

        List<Recipe> searchPasta = repository.searchAndFilter("pasta", "", "", "");
        assertFalse(searchPasta.isEmpty());
        assertTrue(searchPasta.stream().anyMatch(r -> r.getName().toLowerCase().contains("pasta")));
    }

    @Test
    void testCategoryCounts() {
        Map<String, Integer> counts = repository.getCategoryCounts();
        assertNotNull(counts);
        assertTrue(counts.containsKey("Breakfast"));
        assertTrue(counts.containsKey("Dinner"));
        assertTrue(counts.containsKey("Desserts"));
    }
}
