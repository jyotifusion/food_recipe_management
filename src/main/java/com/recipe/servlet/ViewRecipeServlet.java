package com.recipe.servlet;

import com.recipe.dao.RecipeXMLRepository;
import com.recipe.model.Recipe;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller servlet for displaying detailed view of a recipe.
 */
@WebServlet(name = "ViewRecipeServlet", urlPatterns = {"/recipe/view"})
public class ViewRecipeServlet extends HttpServlet {
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
            request.getSession().setAttribute("flashError", "Recipe ID was missing or invalid.");
            response.sendRedirect(request.getContextPath() + "/recipes");
            return;
        }

        Optional<Recipe> recipeOpt = repository.findById(id.trim());
        if (recipeOpt.isEmpty()) {
            request.getSession().setAttribute("flashError", "Recipe not found with ID: " + id);
            response.sendRedirect(request.getContextPath() + "/recipes");
            return;
        }

        Recipe recipe = recipeOpt.get();
        request.setAttribute("recipe", recipe);
        request.setAttribute("pageTitle", recipe.getName() + " - Recipe Details");
        request.setAttribute("activeNav", "recipes");

        request.getRequestDispatcher("/WEB-INF/views/recipe-details.jsp").forward(request, response);
    }
}
