package com.apigenerator.service.util;

import com.apigenerator.exceptions.ErrorType;
import com.apigenerator.generators.GeneratorException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
public class FileUtils {

    private static final String DEFAULT_GENERATED_DIR = "generated";

    /**
     * Obtient le répertoire de génération
     */
    private static Path getGeneratedDir() {
        String configuredDir = System.getProperty("generated.dir");
        return configuredDir != null ? Paths.get(configuredDir)
                : Paths.get(DEFAULT_GENERATED_DIR);
    }

    /**
     * Sauvegarde un fichier dans le répertoire de génération
     */
    public static String saveFile(InputStream inputStream, String originalFileName, String subDirectory) {
        try {
            Path uploadPath = createDirectoryIfNotExists(subDirectory);
            String fileName = generateUniqueFileName(originalFileName);
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new GeneratorException(
                    "Erreur lors de la sauvegarde du fichier : " + originalFileName,
                    ErrorType.IO_ERROR,
                    e
            );
        }
    }

    /**
     * Crée un répertoire s'il n'existe pas
     */
    private static Path createDirectoryIfNotExists(String subDirectory) {
        try {
            Path dirPath = getGeneratedDir().resolve(subDirectory);
            Files.createDirectories(dirPath);
            return dirPath;
        } catch (IOException e) {
            throw new GeneratorException(
                    "Erreur lors de la création du répertoire : " + subDirectory,
                    ErrorType.IO_ERROR,
                    e
            );
        }
    }

    /**
     * Obtient le chemin d'un fichier
     */
    public static Path getFilePath(String fileName, String subDirectory) {
        if (subDirectory.contains("..")) {
            throw new GeneratorException(
                    "Chemin invalide : " + subDirectory,
                    ErrorType.IO_ERROR
            );
        }
        return createDirectoryIfNotExists(subDirectory).resolve(fileName);
    }

    /**
     * Supprime un fichier
     */
    public static void deleteFile(String fileName, String subDirectory) {
        try {
            Path filePath = getFilePath(fileName, subDirectory);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new GeneratorException(
                    "Erreur lors de la suppression du fichier : " + fileName,
                    ErrorType.IO_ERROR,
                    e
            );
        }
    }

    /**
     * Vérifie si un fichier existe
     */
    public static boolean fileExists(String fileName, String subDirectory) {
        return Files.exists(getFilePath(fileName, subDirectory));
    }

    /**
     * Lit le contenu d'un fichier
     */
    public static String readFile(String fileName, String subDirectory) {
        try {
            Path filePath = getFilePath(fileName, subDirectory);
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new GeneratorException(
                    "Erreur lors de la lecture du fichier : " + fileName,
                    ErrorType.IO_ERROR,
                    e
            );
        }
    }

    /**
     * Génère un nom de fichier unique
     */
    private static String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}