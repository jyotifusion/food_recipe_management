<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<footer class="site-footer">
    <div class="container footer-content">
        <div>
            <strong>CulinaryCraft</strong> — Recipe Management System &copy; 2026. Built with Java Servlets, JSP &amp; XML Storage.
        </div>
        <nav class="footer-nav" aria-label="Footer Navigation">
            <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/recipes">All Recipes</a>
            <a href="${pageContext.request.contextPath}/recipe/add">Add Recipe</a>
            <a href="${pageContext.request.contextPath}/about">Architecture &amp; XML</a>
        </nav>
    </div>
</footer>

<!-- Include Delete Modal for all views -->
<jsp:include page="/WEB-INF/views/common/delete-modal.jsp" />

<!-- Main Interactive JavaScript -->
<script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
