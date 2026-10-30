#!/usr/bin/env bash
cd "$(dirname "$0")"
echo "==============================================================="
echo "  Starting Recipe Management System (Tomcat 10.1 Embedded)"
echo "==============================================================="
java -cp "target/classes:target/RecipeManagementSystem/WEB-INF/lib/*" com.recipe.launcher.Main
