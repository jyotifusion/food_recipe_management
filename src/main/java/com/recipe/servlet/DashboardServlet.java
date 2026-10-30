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
import java.util.Map;

/**
 * Controller servlet for the Home Dashboard.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"", "/dashboard"})
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RecipeXMLRepository repository;

    @Override
    public void init() throws ServletException {
        super.init();
        // Allow optional override from ServletContext param
        String customXmlPath = getServletContext().getInitParameter("recipeXmlPath");
        if (customXmlPath != null && !customXmlPath.trim().isEmpty()) {
            RecipeXMLRepository.initialize(customXmlPath);
        }
        repository = RecipeXMLRepository.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int totalRecipes = repository.getTotalCount();
        List<Recipe> recentRecipes = repository.getRecentRecipes(6);
        Map<String, Integer> categoryCounts = repository.getCategoryCounts();

        request.setAttribute("totalRecipes", totalRecipes);
        request.setAttribute("recentRecipes", recentRecipes);
        request.setAttribute("categoryCounts", categoryCounts);
        request.setAttribute("categories", RecipeXMLRepository.CATEGORIES);
        request.setAttribute("xmlStoragePath", repository.getXmlFilePath());
        request.setAttribute("pageTitle", "Dashboard - Recipe Management System");
        request.setAttribute("activeNav", "dashboard");

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
}
