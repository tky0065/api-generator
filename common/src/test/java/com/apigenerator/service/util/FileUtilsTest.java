package com.apigenerator.service.util;

import com.apigenerator.generators.GeneratorException;
import com.apigenerator.service.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @TempDir
    Path tempDir;

    private static final String TEST_CONTENT = "Test content";
    private static final String TEST_SUBDIRECTORY = "test";

    @BeforeEach
    void setUp() {
        System.setProperty("generated.dir", tempDir.toString());
    }

    @Test
    void testSaveAndReadFile() {
        // Créer et sauvegarder le fichier
        String fileName = FileUtils.saveFile(
                new ByteArrayInputStream(TEST_CONTENT.getBytes(StandardCharsets.UTF_8)),
                "test.txt",
                TEST_SUBDIRECTORY
        );

        // Vérifier l'existence et le contenu
        assertTrue(FileUtils.fileExists(fileName, TEST_SUBDIRECTORY));
        assertEquals(TEST_CONTENT, FileUtils.readFile(fileName, TEST_SUBDIRECTORY));
    }

    @Test
    void testDeleteFile() {
        // Créer le fichier
        String fileName = FileUtils.saveFile(
                new ByteArrayInputStream(TEST_CONTENT.getBytes(StandardCharsets.UTF_8)),
                "test.txt",
                TEST_SUBDIRECTORY
        );

        assertTrue(FileUtils.fileExists(fileName, TEST_SUBDIRECTORY));

        // Supprimer et vérifier
        FileUtils.deleteFile(fileName, TEST_SUBDIRECTORY);
        assertFalse(FileUtils.fileExists(fileName, TEST_SUBDIRECTORY));
    }

    @Test
    void testGenerateUniqueFileName() {
        String fileName1 = FileUtils.saveFile(
                new ByteArrayInputStream("Content 1".getBytes(StandardCharsets.UTF_8)),
                "test.txt",
                TEST_SUBDIRECTORY
        );

        String fileName2 = FileUtils.saveFile(
                new ByteArrayInputStream("Content 2".getBytes(StandardCharsets.UTF_8)),
                "test.txt",
                TEST_SUBDIRECTORY
        );

        assertNotEquals(fileName1, fileName2);
    }

    @Test
    void testInvalidDirectory() {
        assertThrows(GeneratorException.class, () ->
                FileUtils.getFilePath("test.txt", "../invalid")
        );
    }
}