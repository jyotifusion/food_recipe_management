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

/**
 * Controller servlet for listing, searching, filtering, and sorting recipes.
 */
@WebServlet(name = "RecipeListServlet", urlPatterns = {"/recipes"})
public class RecipeListServlet extends HttpServlet {
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

        String query = request.getParameter("q");
        String category = request.getParameter("category");
        String difficulty = request.getParameter("difficulty");
        String sortBy = request.getParameter("sortBy");

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id-desc"; // default to newest first
        }

        List<Recipe> recipes = repository.searchAndFilter(query, category, difficulty, sortBy);

        request.setAttribute("recipes", recipes);
        request.setAttribute("searchQuery", query != null ? query : "");
        request.setAttribute("selectedCategory", category != null ? category : "");
        request.setAttribute("selectedDifficulty", difficulty != null ? difficulty : "");
        request.setAttribute("selectedSortBy", sortBy);
        request.setAttribute("categories", RecipeXMLRepository.CATEGORIES);
        request.setAttribute("difficulties", RecipeXMLRepository.DIFFICULTIES);
        request.setAttribute("totalCount", repository.getTotalCount());
        request.setAttribute("pageTitle", "All Recipes - Recipe Management System");
        request.setAttribute("activeNav", "recipes");

        request.getRequestDispatcher("/WEB-INF/views/recipes.jsp").forward(request, response);
    }
}
