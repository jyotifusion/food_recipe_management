<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container" style="margin-top: 18px;">
    <%-- Session Flash Success Message --%>
    <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="alert alert-success auto-dismiss-alert" role="alert">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
            <div>
                <strong>Success!</strong> ${sessionScope.flashSuccess}
            </div>
        </div>
        <% session.removeAttribute("flashSuccess"); %>
    </c:if>

    <%-- Session Flash Error Message --%>
    <c:if test="${not empty sessionScope.flashError}">
        <div class="alert alert-error auto-dismiss-alert" role="alert">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            <div>
                <strong>Attention:</strong> ${sessionScope.flashError}
            </div>
        </div>
        <% session.removeAttribute("flashError"); %>
    </c:if>

    <%-- Form Validation Errors List --%>
    <c:if test="${not empty validationErrors}">
        <div class="alert alert-error" role="alert">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            <div>
                <strong>Please correct the following errors before submitting:</strong>
                <ul>
                    <c:forEach var="err" items="${validationErrors}">
                        <li>${err}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </c:if>
</div>
