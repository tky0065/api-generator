package com.apigenerator.intellij.generators;

import com.intellij.openapi.project.Project;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jetbrains.annotations.NotNull;

public abstract class CodeGenerator {
    protected final Project project;

    protected CodeGenerator(Project project) {
        this.project = project;
    }

    protected void createJavaFile(String packagePath, String className, String content) {
        try {
            // Créer les répertoires nécessaires
            Path directoryPath = Paths.get(String.valueOf(project.getBaseDir()), "src", "main", "java",
                    packagePath.replace('.', File.separatorChar));
            Files.createDirectories(directoryPath);

            // Créer le fichier Java
            Path filePath = directoryPath.resolve(className + ".java");

            StringBuilder fileContent = new StringBuilder()
                    .append("package ").append(packagePath).append(";\n\n")
                    .append(content);

            Files.write(filePath, fileContent.toString().getBytes());

        } catch (IOException e) {
            throw new RuntimeException("Failed to create Java file: " + className, e);
        }
    }

    protected String getPackageName(String fullPath) {
        int lastDotIndex = fullPath.lastIndexOf('.');
        return lastDotIndex > 0 ? fullPath.substring(0, lastDotIndex) : "";
    }

    protected String getClassName(String fullPath) {
        int lastDotIndex = fullPath.lastIndexOf('.');
        return lastDotIndex > 0 ? fullPath.substring(lastDotIndex + 1) : fullPath;
    }

    protected boolean isValidJavaIdentifier(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return Character.isJavaIdentifierStart(name.charAt(0)) &&
                name.chars().skip(1).allMatch(Character::isJavaIdentifierPart);
    }

    protected String sanitizeJavaIdentifier(String name) {
        if (name == null || name.isEmpty()) {
            return "_unknown";
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.isJavaIdentifierStart(name.charAt(0)) ?
                name.charAt(0) : '_');

        name.chars().skip(1)
                .mapToObj(ch -> Character.isJavaIdentifierPart(ch) ?
                        String.valueOf((char)ch) : "_")
                .forEach(result::append);

        return result.toString();
    }
}