package com.recipe.launcher;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Embedded Tomcat 10 Launcher for local development and direct testing.
 * Runs on http://localhost:8080/
 */
public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portProp = System.getProperty("server.port", System.getenv("PORT"));
        if (portProp != null && !portProp.isBlank()) {
            try {
                port = Integer.parseInt(portProp.trim());
            } catch (NumberFormatException ignored) {}
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Initialize default HTTP connector

        // Base directory for Tomcat temp working files
        String baseDir = System.getProperty("catalina.base");
        if (baseDir == null || baseDir.isBlank()) {
            baseDir = new File("target/tomcat").getAbsolutePath();
        }
        File baseDirFile = new File(baseDir);
        baseDirFile.mkdirs();
        tomcat.setBaseDir(baseDirFile.getAbsolutePath());

        // Resolve webapp directory
        String webappDirLocation = "src/main/webapp";
        File webappDir = new File(webappDirLocation);
        if (!webappDir.exists()) {
            webappDir = new File("src/main/webapp");
        }

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDir.getAbsolutePath());
        ctx.setReloadable(true);

        // Add compiled classes to web resource root so servlets and models load cleanly
        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        System.out.println("===============================================================");
        System.out.println("  Starting Recipe Management System on Embedded Tomcat 10.1   ");
        System.out.println("  URL: http://localhost:" + port + "/");
        System.out.println("  Webapp Dir: " + webappDir.getAbsolutePath());
        System.out.println("===============================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
