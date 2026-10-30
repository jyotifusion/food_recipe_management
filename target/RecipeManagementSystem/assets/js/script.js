/**
 * Recipe Management System - Frontend JavaScript
 */

document.addEventListener("DOMContentLoaded", function () {
    // 1. Mobile Menu Toggle
    const mobileMenuBtn = document.getElementById("mobileMenuBtn");
    const navLinks = document.getElementById("navLinks");
    if (mobileMenuBtn && navLinks) {
        mobileMenuBtn.addEventListener("click", function () {
            navLinks.classList.toggle("show");
        });
    }

    // 2. Interactive Ingredient Checkboxes (Recipe Details)
    const ingredientItems = document.querySelectorAll(".ingredient-item");
    ingredientItems.forEach(function (item) {
        const checkbox = item.querySelector("input[type='checkbox']");
        if (checkbox) {
            item.addEventListener("click", function (e) {
                if (e.target !== checkbox) {
                    checkbox.checked = !checkbox.checked;
                }
                if (checkbox.checked) {
                    item.classList.add("checked");
                } else {
                    item.classList.remove("checked");
                }
            });
        }
    });

    // 3. Dynamic Serving Size Adjuster (Recipe Details)
    const baseServingsElem = document.getElementById("baseServingsCount");
    const currentServingsElem = document.getElementById("currentServingsDisplay");
    const increaseServingsBtn = document.getElementById("increaseServingsBtn");
    const decreaseServingsBtn = document.getElementById("decreaseServingsBtn");

    if (baseServingsElem && currentServingsElem && increaseServingsBtn && decreaseServingsBtn) {
        const baseServings = parseInt(baseServingsElem.value, 10) || 1;
        let currentServings = baseServings;

        function updateServings(newServings) {
            if (newServings < 1) return;
            currentServings = newServings;
            currentServingsElem.textContent = currentServings;
            const factor = currentServings / baseServings;

            document.querySelectorAll(".ingredient-text").forEach(function (span) {
                const originalText = span.getAttribute("data-original") || span.textContent;
                if (!span.getAttribute("data-original")) {
                    span.setAttribute("data-original", originalText);
                }

                // Match leading number or fractions (e.g., "250g", "2 cups", "1.5 tsp", "1/2 cup")
                const scaledText = scaleIngredientAmounts(originalText, factor);
                span.textContent = scaledText;
            });
        }

        increaseServingsBtn.addEventListener("click", function () {
            updateServings(currentServings + 1);
        });

        decreaseServingsBtn.addEventListener("click", function () {
            if (currentServings > 1) {
                updateServings(currentServings - 1);
            }
        });
    }

    // 4. Image Preview for Add/Edit Form
    const imageUrlInput = document.getElementById("imageUrlInput");
    const imagePreview = document.getElementById("imagePreview");
    if (imageUrlInput && imagePreview) {
        const updatePreview = function () {
            const val = imageUrlInput.value.trim();
            if (val) {
                imagePreview.src = val;
                imagePreview.style.display = "block";
            }
        };

        imageUrlInput.addEventListener("input", updatePreview);
        imagePreview.addEventListener("error", function () {
            imagePreview.src = "https://images.unsplash.com/photo-1495521821757-a1efb6729352?auto=format&fit=crop&w=800&q=80";
        });
    }

    // 5. Delete Confirmation Modal
    const deleteModal = document.getElementById("deleteModal");
    const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
    const deleteRecipeNameSpan = document.getElementById("deleteRecipeName");
    const deleteForm = document.getElementById("deleteRecipeForm");
    const deleteIdInput = document.getElementById("deleteRecipeIdInput");

    document.querySelectorAll(".open-delete-modal-btn").forEach(function (btn) {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            const id = btn.getAttribute("data-recipe-id");
            const name = btn.getAttribute("data-recipe-name");

            if (deleteRecipeNameSpan) deleteRecipeNameSpan.textContent = name;
            if (deleteIdInput) deleteIdInput.value = id;
            if (deleteModal) deleteModal.classList.add("active");
        });
    });

    const cancelDeleteBtn = document.getElementById("cancelDeleteBtn");
    if (cancelDeleteBtn && deleteModal) {
        cancelDeleteBtn.addEventListener("click", function () {
            deleteModal.classList.remove("active");
        });
    }

    if (deleteModal) {
        deleteModal.addEventListener("click", function (e) {
            if (e.target === deleteModal) {
                deleteModal.classList.remove("active");
            }
        });
    }

    // 6. Print Recipe Helper
    const printBtn = document.getElementById("printRecipeBtn");
    if (printBtn) {
        printBtn.addEventListener("click", function () {
            window.print();
        });
    }

    // 7. Auto-dismiss alerts after 6 seconds
    const flashAlerts = document.querySelectorAll(".auto-dismiss-alert");
    flashAlerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = "opacity 0.5s ease, transform 0.5s ease";
            alert.style.opacity = "0";
            alert.style.transform = "translateY(-10px)";
            setTimeout(function () {
                alert.remove();
            }, 500);
        }, 6000);
    });
});

/**
 * Helper to dynamically scale ingredient quantities based on serving adjustment.
 */
function scaleIngredientAmounts(text, factor) {
    if (factor === 1) return text;

    // Match patterns like "2 cups", "2.5 tbsp", "250g", "1/2 cup"
    return text.replace(/^([\d\.\/]+)(\s*)([a-zA-Z]*)/, function (match, numberPart, space, unit) {
        let val = 0;
        if (numberPart.includes("/")) {
            const parts = numberPart.split("/");
            val = parseFloat(parts[0]) / parseFloat(parts[1]);
        } else {
            val = parseFloat(numberPart);
        }

        if (isNaN(val)) return match;

        let scaled = val * factor;
        let formatted = (Math.round(scaled * 10) / 10).toString();
        // Clean up common whole numbers
        if (formatted.endsWith(".0")) {
            formatted = formatted.substring(0, formatted.length - 2);
        }
        return formatted + space + unit;
    });
}
