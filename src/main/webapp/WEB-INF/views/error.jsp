<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 48px; margin-bottom: 60px;">
    <div class="empty-state" style="max-width: 600px; margin: 0 auto;">
        <div class="empty-state-icon">🍽️</div>
        <h1 class="empty-state-title" style="font-size: 2rem;">Oops! Page or Recipe Not Found</h1>
        <p class="empty-state-desc">
            The page or recipe you are looking for does not exist or an unexpected error occurred while processing your request.
        </p>

        <c:if test="${not empty pageContext.exception}">
            <div style="background: #FFF1F0; color: #D90429; padding: 12px; border-radius: var(--radius-sm); font-family: monospace; font-size: 0.85rem; text-align: left; margin-bottom: 20px; overflow-x: auto;">
                ${pageContext.exception.message}
            </div>
        </c:if>

        <div style="display: flex; gap: 12px; justify-content: center;">
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                Return to Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/recipes" class="btn btn-outline">
                Browse Recipes
            </a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
