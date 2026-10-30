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
 * Controller servlet for securely deleting recipes.
 * Supports POST requests (best practice for state-changing operations).
 */
@WebServlet(name = "DeleteRecipeServlet", urlPatterns = {"/recipe/delete"})
public class DeleteRecipeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RecipeXMLRepository repository;

    @Override
    public void init() throws ServletException {
        super.init();
        repository = RecipeXMLRepository.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processDelete(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Also support GET delete with explicit confirmation flag for link-based triggers
        String confirmed = request.getParameter("confirm");
        if ("true".equalsIgnoreCase(confirmed)) {
            processDelete(request, response);
        } else {
            // Redirect safely if not confirmed
            response.sendRedirect(request.getContextPath() + "/recipes");
        }
    }

    private void processDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String id = request.getParameter("id");
        if (id == null || id.trim().isEmpty()) {
            request.getSession().setAttribute("flashError", "Invalid or missing recipe ID for deletion.");
            response.sendRedirect(request.getContextPath() + "/recipes");
            return;
        }

        Optional<Recipe> opt = repository.findById(id.trim());
        String recipeName = opt.map(Recipe::getName).orElse("Recipe #" + id);

        try {
            boolean deleted = repository.deleteRecipe(id.trim());
            if (deleted) {
                request.getSession().setAttribute("flashSuccess", "Recipe \"" + recipeName + "\" was permanently deleted.");
            } else {
                request.getSession().setAttribute("flashError", "Recipe could not be found or was already removed.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("flashError", "Failed to delete recipe: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/recipes");
    }
}
