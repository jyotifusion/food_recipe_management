package com.recipe.servlet;

import com.recipe.dao.RecipeXMLRepository;
import com.recipe.model.Recipe;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller servlet for editing and updating existing recipes.
 */
@WebServlet(name = "EditRecipeServlet", urlPatterns = {"/recipe/edit"})
public class EditRecipeServlet extends HttpServlet {
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

        String id = request.getParameter("id");
        if (id == null || id.trim().isEmpty()) {
            request.getSession().setAttribute("flashError", "Recipe ID is required for editing.");
            response.sendRedirect(request.getContextPath() + "/recipes");
            return;
        }

        Optional<Recipe> opt = repository.findById(id.trim());
        if (opt.isEmpty()) {
            request.getSession().setAttribute("flashError", "Cannot edit: Recipe with ID " + id + " was not found.");
            response.sendRedirect(request.getContextPath() + "/recipes");
            return;
        }

        Recipe recipe = opt.get();
        request.setAttribute("recipe", recipe);
        request.setAttribute("categories", RecipeXMLRepository.CATEGORIES);
        request.setAttribute("difficulties", RecipeXMLRepository.DIFFICULTIES);
        request.setAttribute("pageTitle", "Edit Recipe: " + recipe.getName());
        request.setAttribute("activeNav", "recipes");

        request.getRequestDispatcher("/WEB-INF/views/edit-recipe.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("id");
        if (id == null || id.trim().isEmpty()) {
            request.getSession().setAttribute("flashError", "Missing recipe ID.");
            response.sendRedirect(request.getContextPath() + "/recipes");
            return;
        }

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

        List<String> ingredients = AddRecipeServlet.parseLines(ingredientsRaw);
        List<String> instructions = AddRecipeServlet.parseLines(instructionsRaw);
        int prepTime = AddRecipeServlet.parsePositiveInt(prepTimeRaw, 0);
        int cookTime = AddRecipeServlet.parsePositiveInt(cookTimeRaw, 0);
        int servings = AddRecipeServlet.parsePositiveInt(servingsRaw, 1);

        Recipe recipe = new Recipe();
        recipe.setId(id.trim());
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
            request.setAttribute("pageTitle", "Edit Recipe: " + recipe.getName());
            request.setAttribute("activeNav", "recipes");
            request.getRequestDispatcher("/WEB-INF/views/edit-recipe.jsp").forward(request, response);
            return;
        }

        try {
            boolean updated = repository.updateRecipe(recipe);
            if (updated) {
                request.getSession().setAttribute("flashSuccess", "Recipe \"" + recipe.getName() + "\" was updated successfully!");
                response.sendRedirect(request.getContextPath() + "/recipe/view?id=" + recipe.getId());
            } else {
                errors.add("Recipe could not be updated. It may have been deleted.");
                request.setAttribute("validationErrors", errors);
                request.setAttribute("recipe", recipe);
                request.setAttribute("categories", RecipeXMLRepository.CATEGORIES);
                request.setAttribute("difficulties", RecipeXMLRepository.DIFFICULTIES);
                request.getRequestDispatcher("/WEB-INF/views/edit-recipe.jsp").forward(request, response);
            }
        } catch (Exception e) {
            errors.add("Error updating recipe: " + e.getMessage());
            request.setAttribute("validationErrors", errors);
            request.setAttribute("recipe", recipe);
            request.setAttribute("categories", RecipeXMLRepository.CATEGORIES);
            request.setAttribute("difficulties", RecipeXMLRepository.DIFFICULTIES);
            request.getRequestDispatcher("/WEB-INF/views/edit-recipe.jsp").forward(request, response);
        }
    }
}
