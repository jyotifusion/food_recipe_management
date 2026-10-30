package com.recipe.servlet;

import com.recipe.dao.RecipeXMLRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller servlet for the About page.
 */
@WebServlet(name = "AboutServlet", urlPatterns = {"/about"})
public class AboutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("xmlPath", RecipeXMLRepository.getInstance().getXmlFilePath());
        request.setAttribute("pageTitle", "About - Recipe Management System");
        request.setAttribute("activeNav", "about");

        request.getRequestDispatcher("/WEB-INF/views/about.jsp").forward(request, response);
    }
}
