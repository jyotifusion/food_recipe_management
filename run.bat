@echo off
set "APP_DIR=%~dp0"
cd /d "%APP_DIR%"
echo ===============================================================
echo   Starting Recipe Management System (Tomcat 10.1 Embedded)
echo ===============================================================
java -cp "target\classes;target\RecipeManagementSystem\WEB-INF\lib\*" com.recipe.launcher.Main
pause
