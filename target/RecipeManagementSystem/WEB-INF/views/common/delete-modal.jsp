<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- Reusable Delete Confirmation Modal -->
<div class="modal-overlay" id="deleteModal" role="dialog" aria-modal="true" aria-labelledby="modalTitle">
    <div class="modal-box">
        <h3 class="modal-header" id="modalTitle">Delete Recipe</h3>
        <p class="modal-body">
            Are you sure you want to permanently delete <strong id="deleteRecipeName">this recipe</strong>? This operation cannot be undone.
        </p>
        <form id="deleteRecipeForm" method="post" action="${pageContext.request.contextPath}/recipe/delete">
            <input type="hidden" name="id" id="deleteRecipeIdInput" value="">
            <div class="modal-actions">
                <button type="button" class="btn btn-outline btn-sm" id="cancelDeleteBtn">Cancel</button>
                <button type="submit" class="btn btn-accent btn-sm" id="confirmDeleteBtn">Yes, Delete Recipe</button>
            </div>
        </form>
    </div>
</div>
