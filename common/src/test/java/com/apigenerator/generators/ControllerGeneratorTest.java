package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ControllerGeneratorTest {

    private ControllerGenerator controllerGenerator;
    private EntityModel sampleEntity;
    private GeneratorConfig config;

    @BeforeEach
    void setUp() {
        controllerGenerator = new ControllerGenerator();

        sampleEntity = EntityModel.builder()
                .packageName("com.example.domain")
                .className("User")
                .tableName("users")
                .fields(Arrays.asList(
                        FieldModel.builder()
                                .name("id")
                                .type("Long")
                                .isPrimary(true)
                                .build(),
                        FieldModel.builder()
                                .name("avatar")
                                .type("byte[]")
                                .columnName("avatar")
                                .build()
                ))
                .build();

        config = GeneratorConfig.builder()
                .generateFileUpload(true)
                .build();
    }

    @Test
    void testGenerateController() {
        String generated = controllerGenerator.generate(sampleEntity, config);

        String[] expectedElements = {
                "@RestController",
                "@RequestMapping(\"/api/users\")",
                "@Tag(name = \"User Management\")",
                "@PostMapping",
                "@PutMapping(\"/{id}\")",
                "@GetMapping",
                "@DeleteMapping(\"/{id}\")",
                "@Operation(summary",
                "ResponseEntity<",
                "ResponseEntity.ok()",
                "ResponseEntity.created("
        };

        for (String element : expectedElements) {
            assertTrue(
                    generated.contains(element),
                    "Le code généré devrait contenir: " + element + "\nCode généré:\n" + generated
            );
        }
    }
    @Test
    void testGenerateFileUploadEndpoints() {
        String generated = controllerGenerator.generate(sampleEntity, config);

        // Vérifier la présence du contrôleur de fichiers
        assertTrue(generated.contains("UserFileResource"),
                "Devrait contenir un contrôleur de fichiers");

        // Vérifier les endpoints
        assertTrue(generated.contains("@PostMapping(\"/upload/{id}\")") ||
                        generated.contains("@PostMapping(\"files/upload/{id}\")") ||
                        generated.contains("@PostMapping(\"/files/upload/{id}\")"),
                "Devrait contenir un endpoint d'upload");

        assertTrue(generated.contains("@GetMapping(\"/download/{id}\")") ||
                        generated.contains("@GetMapping(\"files/download/{id}\")") ||
                        generated.contains("@GetMapping(\"/files/download/{id}\")"),
                "Devrait contenir un endpoint de download");

        // Vérifier les paramètres et types
        assertTrue(generated.contains("MultipartFile file"),
                "Devrait accepter un fichier multipart");

        assertTrue(generated.contains("ResponseEntity<Resource>"),
                "Devrait retourner une Resource pour le download");

        // Vérifier la documentation
        assertTrue(generated.contains("@Operation(summary"),
                "Devrait contenir la documentation Swagger");
    }

    @Test
    void testGetOutputPath() {
        String path = controllerGenerator.getOutputPath(sampleEntity, config);
        assertEquals("com/example/domain/web/rest/UserResource.java", path);
    }
}