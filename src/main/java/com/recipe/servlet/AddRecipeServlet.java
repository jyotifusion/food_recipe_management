package com.recipe.servlet;

import com.recipe.dao.RecipeXMLRepository;
import com.recipe.model.Recipe;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller servlet for creating new recipes.
 */
@WebServlet(name = "AddRecipeServlet", urlPatterns = {"/recipe/add"})
public class AddRecipeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RecipeXMLRepository repository;

    @Override
    public void init() throws ServletException {
        super.init();
        repository = RecipeXMLRepository.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("categories", RecipeXMLRepository.CATEGORIES);
        request.setAttribute("difficulties", RecipeXMLRepository.DIFFICULTIES);
        request.setAttribute("pageTitle", "Add New Recipe - Recipe Management System");
        request.setAttribute("activeNav", "add");

        if (request.getAttribute("recipe") == null) {
            Recipe empty = new Recipe();
            empty.setServings(2);
            empty.setPreparationTime(15);
            empty.setCookingTime(20);
            empty.setDifficulty("Easy");
            request.setAttribute("recipe", empty);
        }

        request.getRequestDispatcher("/WEB-INF/views/add-recipe.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        String ingredientsRaw = request.getParameter("ingredients");
        String instructionsRaw = request.getParameter("instructions");
        String prepTimeRaw = request.getParameter("preparationTime");
        String cookTimeRaw = request.getParameter("cookingTime");
        String servingsRaw = request.getParameter("servings");
        String difficulty = request.getParameter("difficulty");
        String imageUrl = request.getParameter("imageUrl");

        List<String> ingredients = parseLines(ingredientsRaw);
        List<String> instructions = parseLines(instructionsRaw);
        int prepTime = parsePositiveInt(prepTimeRaw, 0);
        int cookTime = parsePositiveInt(cookTimeRaw, 0);
        int servings = parsePositiveInt(servingsRaw, 1);

        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setCategory(category);
        recipe.setDescription(description);
        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);
        recipe.setPreparationTime(prepTime);
        recipe.setCookingTime(cookTime);
        recipe.setServings(servings);
        recipe.setDifficulty(difficulty);
        recipe.setImageUrl(imageUrl);

        List<String> errors = recipe.validate();

        if (!errors.isEmpty()) {
            request.setAttribute("validationErrors", errors);
            request.setAttribute("recipe", recipe);
            request.setAttribute("categories", RecipeXMLRepository.CATEGORIES);
            request.setAttribute("difficulties", RecipeXMLRepository.DIFFICULTIES);
            request.setAttribute("pageTitle", "Add New Recipe - Recipe Management System");
            request.setAttribute("activeNav", "add");
            request.getRequestDispatcher("/WEB-INF/views/add-recipe.jsp").forward(request, response);
            return;
        }

        try {
            Recipe saved = repository.addRecipe(recipe);
            request.getSession().setAttribute("flashSuccess", "Recipe \"" + saved.getName() + "\" has been created successfully!");
            response.sendRedirect(request.getContextPath() + "/recipe/view?id=" + saved.getId());
        } catch (Exception e) {
            errors.add("Failed to save recipe: " + e.getMessage());
            request.setAttribute("validationErrors", errors);
            request.setAttribute("recipe", recipe);
            request.setAttribute("categories", RecipeXMLRepository.CATEGORIES);
            request.setAttribute("difficulties", RecipeXMLRepository.DIFFICULTIES);
            request.setAttribute("pageTitle", "Add New Recipe - Recipe Management System");
            request.setAttribute("activeNav", "add");
            request.getRequestDispatcher("/WEB-INF/views/add-recipe.jsp").forward(request, response);
        }
    }

    static List<String> parseLines(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

    static int parsePositiveInt(String val, int defaultVal) {
        if (val == null || val.trim().isEmpty()) {
            return defaultVal;
        }
        try {
            int num = Integer.parseInt(val.trim());
            return Math.max(0, num);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
