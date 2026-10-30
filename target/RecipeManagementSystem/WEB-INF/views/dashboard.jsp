<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />
<jsp:include page="/WEB-INF/views/common/alerts.jsp" />

<!-- Hero Banner -->
<section class="hero-banner">
    <div class="container hero-content">
        <span class="hero-tagline">Artisanal Recipe Organizer</span>
        <h1 class="hero-title">Simplify your cooking, savor every creation.</h1>
        <p class="hero-subtitle">
            Welcome to CulinaryCraft. Discover, organize, and curate your favorite culinary masterpieces in one seamless, XML-powered digital cookbook.
        </p>
        <div class="hero-buttons">
            <a href="${pageContext.request.contextPath}/recipes" class="btn btn-primary btn-lg">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                    <rect width="18" height="18" x="3" y="3" rx="2"/>
                    <path d="M7 7h10"/>
                    <path d="M7 12h10"/>
                    <path d="M7 17h10"/>
                </svg>
                <span>Browse All Recipes</span>
            </a>
            <a href="${pageContext.request.contextPath}/recipe/add" class="btn btn-outline btn-lg">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="12" y1="8" x2="12" y2="16"/>
                    <line x1="8" y1="12" x2="16" y2="12"/>
                </svg>
                <span>Add New Recipe</span>
            </a>
        </div>
    </div>
</section>

<!-- Stats Grid -->
<div class="container">
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon">
                <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 21a9 9 0 0 0 9-9H3a9 9 0 0 0 9 9Z"/>
                    <path d="M19 12a7 7 0 0 0-14 0"/>
                </svg>
            </div>
            <div>
                <div class="stat-number">${totalRecipes}</div>
                <div class="stat-label">Total Recipes Stored</div>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-icon orange">
                <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5Z"/>
                    <path d="M6 6h10"/>
                    <path d="M6 10h10"/>
                </svg>
            </div>
            <div>
                <div class="stat-number">${categories.size()}</div>
                <div class="stat-label">Cuisine Categories</div>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-icon">
                <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12 6 12 12 16 14"/>
                </svg>
            </div>
            <div>
                <div class="stat-number">&lt; 30m</div>
                <div class="stat-label">Quick Meal Options</div>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-icon orange">
                <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="7 10 12 15 17 10"/>
                    <line x1="12" y1="15" x2="12" y2="3"/>
                </svg>
            </div>
            <div>
                <div class="stat-number">XML</div>
                <div class="stat-label">DOM Data Engine</div>
            </div>
        </div>
    </div>

    <!-- Category Strip -->
    <section class="categories-section">
        <div class="section-header">
            <div>
                <h2 class="section-title">Explore by Category</h2>
                <p class="section-subtitle">Find dishes tailored to your meals throughout the day</p>
            </div>
            <a href="${pageContext.request.contextPath}/recipes" class="btn btn-outline btn-sm">View All Categories &rarr;</a>
        </div>

        <div class="category-chips">
            <c:forEach var="cat" items="${categories}">
                <a href="${pageContext.request.contextPath}/recipes?category=${cat}" class="category-chip">
                    <span>${cat}</span>
                    <span class="count-badge">${categoryCounts[cat] != null ? categoryCounts[cat] : 0}</span>
                </a>
            </c:forEach>
        </div>
    </section>

    <!-- Recently Added Recipes -->
    <section>
        <div class="section-header">
            <div>
                <h2 class="section-title">Recently Added Recipes</h2>
                <p class="section-subtitle">Fresh additions to your recipe catalog</p>
            </div>
            <a href="${pageContext.request.contextPath}/recipes?sortBy=id-desc" class="btn btn-outline btn-sm">Browse All &rarr;</a>
        </div>

        <c:choose>
            <c:when test="${empty recentRecipes}">
                <div class="empty-state">
                    <div class="empty-state-icon">🍳</div>
                    <h3 class="empty-state-title">No Recipes Stored Yet</h3>
                    <p class="empty-state-desc">Your cookbook is empty. Start your culinary journey by adding your first delicious recipe!</p>
                    <a href="${pageContext.request.contextPath}/recipe/add" class="btn btn-primary">
                        Add Your First Recipe
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="recipe-grid">
                    <c:forEach var="recipe" items="${recentRecipes}">
                        <article class="recipe-card">
                            <div class="card-image-wrap">
                                <img src="${recipe.displayImageUrl}" 
                                     alt="${recipe.name}" 
                                     class="card-image"
                                     loading="lazy">
                                <div class="card-badges">
                                    <span class="badge badge-category">${recipe.category}</span>
                                    <span class="badge badge-difficulty badge-${recipe.difficulty.toLowerCase()}">${recipe.difficulty}</span>
                                </div>
                            </div>

                            <div class="card-body">
                                <h3 class="card-title">
                                    <a href="${pageContext.request.contextPath}/recipe/view?id=${recipe.id}">
                                        ${recipe.name}
                                    </a>
                                </h3>
                                <p class="card-desc">${recipe.description}</p>

                                <div class="card-meta">
                                    <div class="meta-item">
                                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <circle cx="12" cy="12" r="10"/>
                                            <polyline points="12 6 12 12 16 14"/>
                                        </svg>
                                        <span>${recipe.formattedTotalTime}</span>
                                    </div>
                                    <div class="meta-item">
                                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                                            <circle cx="9" cy="7" r="4"/>
                                        </svg>
                                        <span>${recipe.servings} Servings</span>
                                    </div>
                                    <div class="meta-item">
                                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <line x1="8" y1="6" x2="21" y2="6"/>
                                            <line x1="8" y1="12" x2="21" y2="12"/>
                                            <line x1="8" y1="18" x2="21" y2="18"/>
                                            <line x1="3" y1="6" x2="3.01" y2="6"/>
                                            <line x1="3" y1="12" x2="3.01" y2="12"/>
                                            <line x1="3" y1="18" x2="3.01" y2="18"/>
                                        </svg>
                                        <span>${recipe.ingredients.size()} Ingr.</span>
                                    </div>
                                </div>
                            </div>

                            <div class="card-actions">
                                <a href="${pageContext.request.contextPath}/recipe/view?id=${recipe.id}" class="btn btn-outline btn-sm">
                                    View Recipe
                                </a>
                                <div class="card-actions-group">
                                    <a href="${pageContext.request.contextPath}/recipe/edit?id=${recipe.id}" class="btn btn-outline btn-sm" title="Edit Recipe" aria-label="Edit ${recipe.name}">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                                        </svg>
                                    </a>
                                    <button type="button" 
                                            class="btn btn-danger-outline btn-sm open-delete-modal-btn" 
                                            data-recipe-id="${recipe.id}"
                                            data-recipe-name="${recipe.name}"
                                            title="Delete Recipe"
                                            aria-label="Delete ${recipe.name}">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <polyline points="3 6 5 6 21 6"/>
                                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                                        </svg>
                                    </button>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
