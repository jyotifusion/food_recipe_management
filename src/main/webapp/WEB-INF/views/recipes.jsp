<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />
<jsp:include page="/WEB-INF/views/common/alerts.jsp" />

<div class="container" style="margin-top: 32px;">
    <!-- Page Header -->
    <div class="section-header">
        <div>
            <h1 class="section-title">All Recipes</h1>
            <p class="section-subtitle">
                Showing <strong>${recipes.size()}</strong> of <strong>${totalCount}</strong> total recipes in your collection
            </p>
        </div>
        <a href="${pageContext.request.contextPath}/recipe/add" class="btn btn-primary">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            <span>Add Recipe</span>
        </a>
    </div>

    <!-- Search & Filter Controls Card -->
    <div class="filter-card">
        <form action="${pageContext.request.contextPath}/recipes" method="get" class="filter-form">
            <!-- Search Query Input -->
            <div class="search-input-group">
                <svg class="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
                <input type="text" 
                       name="q" 
                       class="form-control with-icon" 
                       placeholder="Search by name, ingredient, keyword..." 
                       value="${searchQuery}">
            </div>

            <!-- Category Filter -->
            <div>
                <select name="category" class="form-control" aria-label="Filter by Category">
                    <option value="">All Categories</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat}" ${selectedCategory == cat ? 'selected' : ''}>${cat}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Difficulty Filter -->
            <div>
                <select name="difficulty" class="form-control" aria-label="Filter by Difficulty">
                    <option value="">All Difficulties</option>
                    <c:forEach var="diff" items="${difficulties}">
                        <option value="${diff}" ${selectedDifficulty == diff ? 'selected' : ''}>${diff}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Sort Option -->
            <div>
                <select name="sortBy" class="form-control" aria-label="Sort By">
                    <option value="id-desc" ${selectedSortBy == 'id-desc' ? 'selected' : ''}>Newest First</option>
                    <option value="id-asc" ${selectedSortBy == 'id-asc' ? 'selected' : ''}>Oldest First</option>
                    <option value="name-asc" ${selectedSortBy == 'name-asc' ? 'selected' : ''}>Name (A &rarr; Z)</option>
                    <option value="name-desc" ${selectedSortBy == 'name-desc' ? 'selected' : ''}>Name (Z &rarr; A)</option>
                    <option value="time-asc" ${selectedSortBy == 'time-asc' ? 'selected' : ''}>Cooking Time (Low to High)</option>
                    <option value="time-desc" ${selectedSortBy == 'time-desc' ? 'selected' : ''}>Cooking Time (High to Low)</option>
                </select>
            </div>

            <!-- Filter Buttons -->
            <div style="display: flex; gap: 8px;">
                <button type="submit" class="btn btn-primary btn-sm">Filter</button>
                <c:if test="${not empty searchQuery or not empty selectedCategory or not empty selectedDifficulty}">
                    <a href="${pageContext.request.contextPath}/recipes" class="btn btn-outline btn-sm" title="Clear filters">Reset</a>
                </c:if>
            </div>
        </form>
    </div>

    <!-- Recipes Grid -->
    <c:choose>
        <c:when test="${empty recipes}">
            <div class="empty-state">
                <div class="empty-state-icon">🔍</div>
                <h3 class="empty-state-title">No Recipes Found</h3>
                <p class="empty-state-desc">
                    We couldn't find any recipes matching your search or filters. Try adjusting your search criteria or add a new recipe!
                </p>
                <div style="display: flex; gap: 12px; justify-content: center;">
                    <a href="${pageContext.request.contextPath}/recipes" class="btn btn-outline">Clear All Filters</a>
                    <a href="${pageContext.request.contextPath}/recipe/add" class="btn btn-primary">Add New Recipe</a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="recipe-grid">
                <c:forEach var="recipe" items="${recipes}">
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
                                View Details
                            </a>
                            <div class="card-actions-group">
                                <a href="${pageContext.request.contextPath}/recipe/edit?id=${recipe.id}" 
                                   class="btn btn-outline btn-sm" 
                                   title="Edit Recipe"
                                   aria-label="Edit ${recipe.name}">
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
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
