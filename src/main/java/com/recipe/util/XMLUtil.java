package com.recipe.util;

import com.recipe.model.Recipe;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe XML utility class providing DOM-based parsing, writing,
 * and querying of recipes with strict XXE protection.
 */
public class XMLUtil {
    private static final Logger LOGGER = Logger.getLogger(XMLUtil.class.getName());
    private static final Object FILE_LOCK = new Object();

    /**
     * Creates a hardened DocumentBuilder protected against XML External Entity (XXE) attacks.
     */
    public static DocumentBuilder createSecureDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        // XXE Protection Features
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.WARNING, "Some XXE protection features not supported by parser: {0}", e.getMessage());
        }

        return factory.newDocumentBuilder();
    }

    /**
     * Creates a hardened Transformer for serializing DOM trees to XML files.
     */
    public static Transformer createSecureTransformer() throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.FINE, "External access attributes not supported on TransformerFactory");
        }

        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        return transformer;
    }

    /**
     * Reads all recipes from the given XML file.
     * If the file does not exist or is empty, returns an empty list.
     */
    public static List<Recipe> readRecipes(File xmlFile) throws IOException {
        synchronized (FILE_LOCK) {
            List<Recipe> recipes = new ArrayList<>();
            if (xmlFile == null || !xmlFile.exists() || xmlFile.length() == 0) {
                return recipes;
            }

            try (InputStream is = new FileInputStream(xmlFile)) {
                DocumentBuilder builder = createSecureDocumentBuilder();
                Document doc = builder.parse(is);
                doc.getDocumentElement().normalize();

                NodeList recipeNodes = doc.getElementsByTagName("recipe");
                for (int i = 0; i < recipeNodes.getLength(); i++) {
                    Node node = recipeNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Recipe recipe = parseRecipeElement((Element) node);
                        if (recipe != null) {
                            recipes.add(recipe);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException e) {
                LOGGER.log(Level.SEVERE, "Failed to parse XML file: " + xmlFile.getAbsolutePath(), e);
                throw new IOException("Malformed or unparseable XML: " + e.getMessage(), e);
            }

            return recipes;
        }
    }

    /**
     * Reads recipes from an InputStream (useful for seed / resource reading).
     */
    public static List<Recipe> readRecipes(InputStream is) throws IOException {
        List<Recipe> recipes = new ArrayList<>();
        if (is == null) {
            return recipes;
        }

        try {
            DocumentBuilder builder = createSecureDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList recipeNodes = doc.getElementsByTagName("recipe");
            for (int i = 0; i < recipeNodes.getLength(); i++) {
                Node node = recipeNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Recipe recipe = parseRecipeElement((Element) node);
                    if (recipe != null) {
                        recipes.add(recipe);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Malformed XML stream: " + e.getMessage(), e);
        }

        return recipes;
    }

    /**
     * Writes the entire list of recipes back into the XML file atomically.
     */
    public static void writeRecipes(File xmlFile, List<Recipe> recipes) throws IOException {
        synchronized (FILE_LOCK) {
            if (xmlFile == null) {
                throw new IOException("Target XML file cannot be null.");
            }

            File parentDir = xmlFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    LOGGER.warning("Could not create parent directories for " + xmlFile.getAbsolutePath());
                }
            }

            try {
                DocumentBuilder builder = createSecureDocumentBuilder();
                Document doc = builder.newDocument();

                Element root = doc.createElement("recipes");
                doc.appendChild(root);

                if (recipes != null) {
                    for (Recipe r : recipes) {
                        Element recipeElem = createRecipeElement(doc, r);
                        root.appendChild(recipeElem);
                    }
                }

                // Write out with atomic temp-file replace if possible, or direct stream
                Transformer transformer = createSecureTransformer();
                try (OutputStream os = new FileOutputStream(xmlFile)) {
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(os);
                    transformer.transform(source, result);
                }
            } catch (ParserConfigurationException | TransformerException e) {
                LOGGER.log(Level.SEVERE, "Failed to write recipes to XML file: " + xmlFile.getAbsolutePath(), e);
                throw new IOException("Error serializing XML: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Parses an individual <recipe> element into a Recipe model.
     */
    private static Recipe parseRecipeElement(Element element) {
        try {
            String id = element.getAttribute("id");
            String name = getChildText(element, "name");
            String category = getChildText(element, "category");
            String description = getChildText(element, "description");
            String prepTimeStr = getChildText(element, "preparationTime");
            String cookTimeStr = getChildText(element, "cookingTime");
            String servingsStr = getChildText(element, "servings");
            String difficulty = getChildText(element, "difficulty");
            String imageUrl = getChildText(element, "imageUrl");

            List<String> ingredients = new ArrayList<>();
            NodeList ingNodes = element.getElementsByTagName("ingredient");
            for (int i = 0; i < ingNodes.getLength(); i++) {
                Node n = ingNodes.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    String ing = n.getTextContent();
                    if (ing != null && !ing.trim().isEmpty()) {
                        ingredients.add(ing.trim());
                    }
                }
            }

            List<String> instructions = new ArrayList<>();
            NodeList stepNodes = element.getElementsByTagName("step");
            for (int i = 0; i < stepNodes.getLength(); i++) {
                Node n = stepNodes.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    String step = n.getTextContent();
                    if (step != null && !step.trim().isEmpty()) {
                        instructions.add(step.trim());
                    }
                }
            }

            int prepTime = parseInt(prepTimeStr, 0);
            int cookTime = parseInt(cookTimeStr, 0);
            int servings = parseInt(servingsStr, 1);

            return new Recipe(id, name, category, description, ingredients, instructions,
                    prepTime, cookTime, servings, difficulty, imageUrl);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error parsing recipe element: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Creates an XML Element representing a Recipe.
     */
    private static Element createRecipeElement(Document doc, Recipe r) {
        Element recipeElem = doc.createElement("recipe");
        recipeElem.setAttribute("id", r.getId() != null ? r.getId() : "");

        appendElement(doc, recipeElem, "name", r.getName());
        appendElement(doc, recipeElem, "category", r.getCategory());
        appendElement(doc, recipeElem, "description", r.getDescription());

        // Ingredients
        Element ingredientsElem = doc.createElement("ingredients");
        if (r.getIngredients() != null) {
            for (String ing : r.getIngredients()) {
                if (ing != null && !ing.trim().isEmpty()) {
                    appendElement(doc, ingredientsElem, "ingredient", ing.trim());
                }
            }
        }
        recipeElem.appendChild(ingredientsElem);

        // Instructions
        Element instructionsElem = doc.createElement("instructions");
        if (r.getInstructions() != null) {
            for (String step : r.getInstructions()) {
                if (step != null && !step.trim().isEmpty()) {
                    appendElement(doc, instructionsElem, "step", step.trim());
                }
            }
        }
        recipeElem.appendChild(instructionsElem);

        appendElement(doc, recipeElem, "preparationTime", String.valueOf(r.getPreparationTime()));
        appendElement(doc, recipeElem, "cookingTime", String.valueOf(r.getCookingTime()));
        appendElement(doc, recipeElem, "servings", String.valueOf(r.getServings()));
        appendElement(doc, recipeElem, "difficulty", r.getDifficulty());
        appendElement(doc, recipeElem, "imageUrl", r.getImageUrl() != null ? r.getImageUrl() : "");

        return recipeElem;
    }

    private static void appendElement(Document doc, Element parent, String tagName, String text) {
        Element elem = doc.createElement(tagName);
        elem.setTextContent(text != null ? text : "");
        parent.appendChild(elem);
    }

    private static String getChildText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return "";
    }

    private static int parseInt(String val, int defaultVal) {
        if (val == null || val.trim().isEmpty()) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Filters and sorts recipes in-memory based on search parameters.
     */
    public static List<Recipe> filterAndSort(List<Recipe> allRecipes,
                                             String query,
                                             String category,
                                             String difficulty,
                                             String sortBy) {
        if (allRecipes == null) return new ArrayList<>();

        List<Recipe> filtered = new ArrayList<>();
        String q = query != null ? query.trim().toLowerCase(Locale.ROOT) : "";
        String catFilter = category != null ? category.trim() : "";
        String diffFilter = difficulty != null ? difficulty.trim() : "";

        for (Recipe r : allRecipes) {
            // Category filter
            if (!catFilter.isEmpty() && !"all".equalsIgnoreCase(catFilter)) {
                if (!r.getCategory().equalsIgnoreCase(catFilter)) {
                    continue;
                }
            }

            // Difficulty filter
            if (!diffFilter.isEmpty() && !"all".equalsIgnoreCase(diffFilter)) {
                if (!r.getDifficulty().equalsIgnoreCase(diffFilter)) {
                    continue;
                }
            }

            // Search query filter (search by name, ingredient, description, or category)
            if (!q.isEmpty()) {
                boolean matchesName = r.getName() != null && r.getName().toLowerCase(Locale.ROOT).contains(q);
                boolean matchesDesc = r.getDescription() != null && r.getDescription().toLowerCase(Locale.ROOT).contains(q);
                boolean matchesCat = r.getCategory() != null && r.getCategory().toLowerCase(Locale.ROOT).contains(q);
                boolean matchesIngredient = false;
                if (r.getIngredients() != null) {
                    for (String ing : r.getIngredients()) {
                        if (ing != null && ing.toLowerCase(Locale.ROOT).contains(q)) {
                            matchesIngredient = true;
                            break;
                        }
                    }
                }

                if (!matchesName && !matchesDesc && !matchesCat && !matchesIngredient) {
                    continue;
                }
            }

            filtered.add(r);
        }

        // Sorting
        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "name-asc" -> filtered.sort(Comparator.comparing(r -> r.getName().toLowerCase(Locale.ROOT)));
                case "name-desc" -> filtered.sort(Comparator.comparing((Recipe r) -> r.getName().toLowerCase(Locale.ROOT)).reversed());
                case "time-asc" -> filtered.sort(Comparator.comparingInt(Recipe::getTotalTime));
                case "time-desc" -> filtered.sort(Comparator.comparingInt(Recipe::getTotalTime).reversed());
                case "id-desc" -> filtered.sort((a, b) -> {
                    try {
                        return Integer.compare(Integer.parseInt(b.getId()), Integer.parseInt(a.getId()));
                    } catch (Exception e) {
                        return b.getId().compareTo(a.getId());
                    }
                });
                case "id-asc" -> filtered.sort((a, b) -> {
                    try {
                        return Integer.compare(Integer.parseInt(a.getId()), Integer.parseInt(b.getId()));
                    } catch (Exception e) {
                        return a.getId().compareTo(b.getId());
                    }
                });
                default -> {} // Default order as stored in XML
            }
        }

        return filtered;
    }
}
