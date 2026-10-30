<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />
<jsp:include page="/WEB-INF/views/common/alerts.jsp" />

<div class="container">
    <div class="form-container-card">
        <div class="form-header">
            <h1 class="form-title">Edit Recipe</h1>
            <p class="form-desc">Modify details for "<strong>${recipe.name}</strong>" (ID: ${recipe.id})</p>
        </div>

        <form action="${pageContext.request.contextPath}/recipe/edit" method="post">
            <!-- Hidden ID Field -->
            <input type="hidden" name="id" value="${recipe.id}">

            <!-- Name & Category -->
            <div class="form-grid-2">
                <div class="form-group">
                    <label for="recipeName" class="form-label">Recipe Name <span class="required">*</span></label>
                    <input type="text" 
                           id="recipeName" 
                           name="name" 
                           class="form-control" 
                           value="${recipe.name}" 
                           required 
                           maxlength="100">
                </div>

                <div class="form-group">
                    <label for="recipeCategory" class="form-label">Category <span class="required">*</span></label>
                    <select id="recipeCategory" name="category" class="form-control" required>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat}" ${recipe.category == cat ? 'selected' : ''}>${cat}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <!-- Short Description -->
            <div class="form-group">
                <label for="recipeDesc" class="form-label">Short Description <span class="required">*</span></label>
                <textarea id="recipeDesc" 
                          name="description" 
                          class="form-control" 
                          style="min-height: 80px;" 
                          required 
                          maxlength="500">${recipe.description}</textarea>
            </div>

            <!-- Times, Servings & Difficulty -->
            <div class="form-grid-2">
                <div class="form-grid-2">
                    <div class="form-group">
                        <label for="prepTime" class="form-label">Prep Time (mins)</label>
                        <input type="number" 
                               id="prepTime" 
                               name="preparationTime" 
                               class="form-control" 
                               min="0" 
                               value="${recipe.preparationTime}">
                    </div>

                    <div class="form-group">
                        <label for="cookTime" class="form-label">Cook Time (mins)</label>
                        <input type="number" 
                               id="cookTime" 
                               name="cookingTime" 
                               class="form-control" 
                               min="0" 
                               value="${recipe.cookingTime}">
                    </div>
                </div>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label for="servings" class="form-label">Servings <span class="required">*</span></label>
                        <input type="number" 
                               id="servings" 
                               name="servings" 
                               class="form-control" 
                               min="1" 
                               value="${recipe.servings}" 
                               required>
                    </div>

                    <div class="form-group">
                        <label for="difficulty" class="form-label">Difficulty</label>
                        <select id="difficulty" name="difficulty" class="form-control">
                            <c:forEach var="diff" items="${difficulties}">
                                <option value="${diff}" ${recipe.difficulty == diff ? 'selected' : ''}>${diff}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Image URL with Live Preview -->
            <div class="form-group">
                <label for="imageUrlInput" class="form-label">Recipe Image URL (optional)</label>
                <input type="url" 
                       id="imageUrlInput" 
                       name="imageUrl" 
                       class="form-control" 
                       value="${recipe.imageUrl}">
                <p class="form-hint">Paste an image link or leave blank to use the high-quality category default.</p>
                <div style="margin-top: 10px;">
                    <img id="imagePreview" 
                         src="${not empty recipe.imageUrl ? recipe.imageUrl : recipe.displayImageUrl}" 
                         alt="Image Preview" 
                         style="max-width: 200px; max-height: 130px; object-fit: cover; border-radius: var(--radius-sm); border: 1px solid var(--border-light); display: block;">
                </div>
            </div>

            <!-- Ingredients -->
            <div class="form-group">
                <label for="recipeIngredients" class="form-label">Ingredients <span class="required">*</span></label>
                <textarea id="recipeIngredients" 
                          name="ingredients" 
                          class="form-control" 
                          rows="6" 
                          required>${recipe.ingredientsAsText}</textarea>
                <p class="form-hint">Tip: Enter one ingredient per line.</p>
            </div>

            <!-- Instructions -->
            <div class="form-group">
                <label for="recipeInstructions" class="form-label">Preparation Steps <span class="required">*</span></label>
                <textarea id="recipeInstructions" 
                          name="instructions" 
                          class="form-control" 
                          rows="6" 
                          required>${recipe.instructionsAsText}</textarea>
                <p class="form-hint">Tip: Enter each sequential preparation step on a new line.</p>
            </div>

            <!-- Form Action Buttons -->
            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/recipe/view?id=${recipe.id}" class="btn btn-outline">Cancel</a>
                <button type="submit" class="btn btn-primary btn-lg">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                        <polyline points="17 21 17 13 7 13 7 21"/>
                        <polyline points="7 3 7 8 15 8"/>
                    </svg>
                    <span>Update Recipe</span>
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
