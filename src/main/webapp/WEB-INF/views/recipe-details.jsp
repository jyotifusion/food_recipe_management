<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />
<jsp:include page="/WEB-INF/views/common/alerts.jsp" />

<!-- Hidden base servings value for dynamic scaler JS -->
<input type="hidden" id="baseServingsCount" value="${recipe.servings}">

<div class="container">
    <!-- Breadcrumb / Back Link -->
    <div style="margin-top: 24px; margin-bottom: 16px;">
        <a href="${pageContext.request.contextPath}/recipes" style="display: inline-flex; align-items: center; gap: 6px; font-weight: 600; font-size: 0.9rem;">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="19" y1="12" x2="5" y2="12"/>
                <polyline points="12 19 5 12 12 5"/>
            </svg>
            <span>Back to All Recipes</span>
        </a>
    </div>

    <!-- Recipe Hero Banner Grid -->
    <section class="recipe-detail-header" style="background: transparent; border-bottom: none; padding-top: 10px;">
        <div class="recipe-hero-grid">
            <div class="detail-image-wrap">
                <img src="${recipe.displayImageUrl}" 
                     alt="${recipe.name}" 
                     class="detail-image">
            </div>

            <div>
                <div style="display: flex; gap: 10px; margin-bottom: 14px;">
                    <span class="badge badge-category" style="font-size: 0.85rem; padding: 6px 14px;">${recipe.category}</span>
                    <span class="badge badge-difficulty badge-${recipe.difficulty.toLowerCase()}" style="font-size: 0.85rem; padding: 6px 14px;">${recipe.difficulty} Difficulty</span>
                </div>

                <h1 class="detail-title">${recipe.name}</h1>
                <p class="detail-desc">${recipe.description}</p>

                <!-- Time & Servings Meta Stats -->
                <div class="meta-stats-row">
                    <div class="meta-stat">
                        <div class="meta-stat-label">Prep Time</div>
                        <div class="meta-stat-val">${recipe.formattedPrepTime}</div>
                    </div>
                    <div class="meta-stat">
                        <div class="meta-stat-label">Cook Time</div>
                        <div class="meta-stat-val">${recipe.formattedCookTime}</div>
                    </div>
                    <div class="meta-stat">
                        <div class="meta-stat-label">Total Time</div>
                        <div class="meta-stat-val">${recipe.formattedTotalTime}</div>
                    </div>
                    <div class="meta-stat">
                        <div class="meta-stat-label">Servings</div>
                        <div class="meta-stat-val" style="display: flex; align-items: center; justify-content: center; gap: 6px;">
                            <button type="button" id="decreaseServingsBtn" class="btn btn-outline btn-sm" style="padding: 2px 8px; font-size: 0.75rem;" title="Decrease servings">-</button>
                            <span id="currentServingsDisplay">${recipe.servings}</span>
                            <button type="button" id="increaseServingsBtn" class="btn btn-outline btn-sm" style="padding: 2px 8px; font-size: 0.75rem;" title="Increase servings">+</button>
                        </div>
                    </div>
                </div>

                <!-- Action Buttons Bar -->
                <div class="detail-actions-bar">
                    <a href="${pageContext.request.contextPath}/recipe/edit?id=${recipe.id}" class="btn btn-primary btn-sm">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                        </svg>
                        <span>Edit Recipe</span>
                    </a>

                    <button type="button" class="btn btn-outline btn-sm" id="printRecipeBtn">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="6 9 6 2 18 2 18 9"/>
                            <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/>
                            <rect width="12" height="8" x="6" y="14"/>
                        </svg>
                        <span>Print</span>
                    </button>

                    <button type="button" 
                            class="btn btn-danger-outline btn-sm open-delete-modal-btn" 
                            data-recipe-id="${recipe.id}"
                            data-recipe-name="${recipe.name}">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="3 6 5 6 21 6"/>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                        </svg>
                        <span>Delete</span>
                    </button>
                </div>
            </div>
        </div>
    </section>

    <!-- Recipe Content Grid (Ingredients & Instructions) -->
    <div class="recipe-detail-content">
        <!-- Ingredients Section with Interactive Checklist -->
        <div class="detail-card">
            <h2 class="detail-card-title">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="8" y1="6" x2="21" y2="6"/>
                    <line x1="8" y1="12" x2="21" y2="12"/>
                    <line x1="8" y1="18" x2="21" y2="18"/>
                    <line x1="3" y1="6" x2="3.01" y2="6"/>
                    <line x1="3" y1="12" x2="3.01" y2="12"/>
                    <line x1="3" y1="18" x2="3.01" y2="18"/>
                </svg>
                <span>Ingredients (${recipe.ingredients.size()})</span>
            </h2>
            <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 14px;">
                Tip: Click any ingredient to cross it off as you prepare.
            </p>

            <ul class="ingredients-list">
                <c:forEach var="ingredient" items="${recipe.ingredients}">
                    <li class="ingredient-item">
                        <input type="checkbox" aria-label="Mark ingredient done">
                        <span class="ingredient-text">${ingredient}</span>
                    </li>
                </c:forEach>
            </ul>
        </div>

        <!-- Instructions Section with Step Counter -->
        <div class="detail-card">
            <h2 class="detail-card-title">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 20h9"/>
                    <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/>
                </svg>
                <span>Preparation Instructions (${recipe.instructions.size()} steps)</span>
            </h2>

            <ol class="instructions-list">
                <c:forEach var="step" items="${recipe.instructions}">
                    <li class="instruction-step">${step}</li>
                </c:forEach>
            </ol>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
