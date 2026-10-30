package com.recipe.dao;

import com.recipe.model.Recipe;
import com.recipe.util.XMLUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (Repository) managing Recipe persistence to an external writable XML file.
 * Thread-safe with read/write locking and automatic seed data initialization.
 */
public class RecipeXMLRepository {
    private static final Logger LOGGER = Logger.getLogger(RecipeXMLRepository.class.getName());

    private static volatile RecipeXMLRepository instance;
    private final File xmlFile;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    /**
     * Categories supported by the application.
     */
    public static final List<String> CATEGORIES = List.of(
            "Breakfast",
            "Lunch",
            "Dinner",
            "Desserts",
            "Snacks",
            "Beverages"
    );

    /**
     * Difficulties supported by the application.
     */
    public static final List<String> DIFFICULTIES = List.of(
            "Easy",
            "Medium",
            "Hard"
    );

    private RecipeXMLRepository(String customPath) {
        this.xmlFile = resolveXmlStorageFile(customPath);
        initStorage();
    }

    /**
     * Returns the singleton repository instance using default/configured path.
     */
    public static RecipeXMLRepository getInstance() {
        if (instance == null) {
            synchronized (RecipeXMLRepository.class) {
                if (instance == null) {
                    instance = new RecipeXMLRepository(null);
                }
            }
        }
        return instance;
    }

    /**
     * Initializes repository with an explicit file path (e.g., from web.xml context-param).
     */
    public static synchronized void initialize(String customPath) {
        if (instance == null || (customPath != null && !instance.xmlFile.getAbsolutePath().equals(customPath))) {
            instance = new RecipeXMLRepository(customPath);
        }
    }

    /**
     * Resolves the storage XML file path using the priority:
     * 1. Explicit parameter (from ServletContext)
     * 2. System property (-Drecipe.xml.path=...)
     * 3. Environment variable (RECIPE_XML_PATH)
     * 4. User home directory default: <user.home>/.recipe_management_system/recipes.xml
     */
    private File resolveXmlStorageFile(String explicitPath) {
        if (explicitPath != null && !explicitPath.trim().isEmpty()) {
            return new File(explicitPath.trim());
        }

        String sysProp = System.getProperty("recipe.xml.path");
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return new File(sysProp.trim());
        }

        String envVar = System.getenv("RECIPE_XML_PATH");
        if (envVar != null && !envVar.trim().isEmpty()) {
            return new File(envVar.trim());
        }

        String userHome = System.getProperty("user.home", ".");
        File defaultDir = new File(userHome, ".recipe_management_system");
        return new File(defaultDir, "recipes.xml");
    }

    /**
     * Initializes external storage: creates directories and seeds with default data if missing.
     */
    private void initStorage() {
        rwLock.writeLock().lock();
        try {
            LOGGER.info("Recipe XML storage file path: " + xmlFile.getAbsolutePath());

            if (!xmlFile.exists() || xmlFile.length() == 0) {
                File parentDir = xmlFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    boolean created = parentDir.mkdirs();
                    if (created) {
                        LOGGER.info("Created directory for recipes: " + parentDir.getAbsolutePath());
                    }
                }

                // Copy default seed recipes from classpath resource
                try (InputStream seedStream = getClass().getResourceAsStream("/recipes.xml")) {
                    if (seedStream != null) {
                        try (OutputStream out = new FileOutputStream(xmlFile)) {
                            seedStream.transferTo(out);
                            LOGGER.info("Initialized XML storage with seed recipes from resources.");
                            return;
                        }
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Could not copy seed recipes from classpath: " + e.getMessage(), e);
                }

                // Fallback: create empty XML document
                try {
                    XMLUtil.writeRecipes(xmlFile, Collections.emptyList());
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to initialize blank XML file", e);
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public String getXmlFilePath() {
        return xmlFile.getAbsolutePath();
    }

    /**
     * Retrieves all recipes.
     */
    public List<Recipe> getAllRecipes() {
        rwLock.readLock().lock();
        try {
            return XMLUtil.readRecipes(xmlFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading recipes from " + xmlFile.getAbsolutePath(), e);
            return new ArrayList<>();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Finds a single recipe by its unique ID.
     */
    public Optional<Recipe> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return getAllRecipes().stream()
                .filter(r -> r.getId() != null && r.getId().equals(id.trim()))
                .findFirst();
    }

    /**
     * Generates the next available unique recipe ID (numeric sequence).
     */
    private String generateNextId(List<Recipe> existing) {
        int maxId = 0;
        for (Recipe r : existing) {
            try {
                int curr = Integer.parseInt(r.getId());
                if (curr > maxId) {
                    maxId = curr;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return String.valueOf(maxId + 1);
    }

    /**
     * Adds a new recipe to storage.
     */
    public Recipe addRecipe(Recipe recipe) throws IOException {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null.");
        }

        rwLock.writeLock().lock();
        try {
            List<Recipe> recipes = XMLUtil.readRecipes(xmlFile);
            if (recipe.getId() == null || recipe.getId().trim().isEmpty()) {
                recipe.setId(generateNextId(recipes));
            } else {
                // Check if ID already exists
                boolean exists = recipes.stream().anyMatch(r -> r.getId().equals(recipe.getId()));
                if (exists) {
                    recipe.setId(generateNextId(recipes));
                }
            }

            recipes.add(recipe);
            XMLUtil.writeRecipes(xmlFile, recipes);
            LOGGER.info("Saved new recipe ID: " + recipe.getId() + " - " + recipe.getName());
            return recipe;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Updates an existing recipe by ID.
     */
    public boolean updateRecipe(Recipe recipe) throws IOException {
        if (recipe == null || recipe.getId() == null) {
            return false;
        }

        rwLock.writeLock().lock();
        try {
            List<Recipe> recipes = XMLUtil.readRecipes(xmlFile);
            boolean found = false;
            for (int i = 0; i < recipes.size(); i++) {
                if (recipes.get(i).getId().equals(recipe.getId())) {
                    recipes.set(i, recipe);
                    found = true;
                    break;
                }
            }

            if (found) {
                XMLUtil.writeRecipes(xmlFile, recipes);
                LOGGER.info("Updated recipe ID: " + recipe.getId());
                return true;
            }
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Deletes a recipe by its ID.
     */
    public boolean deleteRecipe(String id) throws IOException {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        rwLock.writeLock().lock();
        try {
            List<Recipe> recipes = XMLUtil.readRecipes(xmlFile);
            boolean removed = recipes.removeIf(r -> r.getId() != null && r.getId().equals(id.trim()));

            if (removed) {
                XMLUtil.writeRecipes(xmlFile, recipes);
                LOGGER.info("Deleted recipe ID: " + id);
                return true;
            }
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Searches, filters, and sorts recipes according to criteria.
     */
    public List<Recipe> searchAndFilter(String query, String category, String difficulty, String sortBy) {
        List<Recipe> all = getAllRecipes();
        return XMLUtil.filterAndSort(all, query, category, difficulty, sortBy);
    }

    /**
     * Gets total recipe count.
     */
    public int getTotalCount() {
        return getAllRecipes().size();
    }

    /**
     * Gets the most recently added recipes (based on descending ID).
     */
    public List<Recipe> getRecentRecipes(int limit) {
        List<Recipe> all = new ArrayList<>(getAllRecipes());
        all.sort((a, b) -> {
            try {
                return Integer.compare(Integer.parseInt(b.getId()), Integer.parseInt(a.getId()));
            } catch (Exception e) {
                return b.getId().compareTo(a.getId());
            }
        });

        if (all.size() <= limit) {
            return all;
        }
        return all.subList(0, limit);
    }

    /**
     * Returns recipe count per category.
     */
    public Map<String, Integer> getCategoryCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String cat : CATEGORIES) {
            counts.put(cat, 0);
        }

        for (Recipe r : getAllRecipes()) {
            String cat = r.getCategory();
            if (cat != null) {
                counts.put(cat, counts.getOrDefault(cat, 0) + 1);
            }
        }
        return counts;
    }
}
