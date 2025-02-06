
package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DtoGeneratorTest {

    private DtoGenerator dtoGenerator;
    private EntityModel sampleEntity;
    private GeneratorConfig config;

    @BeforeEach
    void setUp() {
        dtoGenerator = new DtoGenerator();

        // Configurer l'entité de test
        sampleEntity = EntityModel.builder()
                .packageName("com.example.domain")
                .className("User")
                .tableName("users")
                .hasLombok(true)
                .fields(Arrays.asList(
                        FieldModel.builder()
                                .name("id")
                                .type("Long")
                                .isPrimary(true)
                                .build(),
                        FieldModel.builder()
                                .name("firstName")
                                .type("String")
                                .columnName("first_name")
                                .isNullable(false)
                                .build(),
                        FieldModel.builder()
                                .name("email")
                                .type("String")
                                .columnName("email")
                                .build()
                ))
                .build();

        config = GeneratorConfig.builder()
                .useLombok(true)
                .dtoSuffix("DTO")
                .build();
    }

    @Test
    void testGenerateDto() {
        String generated = dtoGenerator.generate(sampleEntity, config);

        // Vérifier la présence des éléments essentiels
        assertTrue(generated.contains("package com.example.domain.dto"));
        assertTrue(generated.contains("public class UserDTO"));
        assertTrue(generated.contains("private Long id"));
        assertTrue(generated.contains("private String firstName"));
        assertTrue(generated.contains("private String email"));

        // Vérifier la présence des annotations Lombok
        assertTrue(generated.contains("@Data"));

        // Vérifier l'absence de getters/setters explicites (car Lombok est utilisé)
        assertFalse(generated.contains("public Long getId()"));
    }

    @Test
    void testGenerateDtoWithoutLombok() {
        config = GeneratorConfig.builder()
                .useLombok(false)
                .dtoSuffix("DTO")
                .build();

        String generated = dtoGenerator.generate(sampleEntity, config);

        // Vérifier l'absence des annotations Lombok
        assertFalse(generated.contains("@Data"));

        // Vérifier la présence des getters/setters
        assertTrue(generated.contains("public Long getId()"));
        assertTrue(generated.contains("public void setId(Long id)"));
    }

    @Test
    void testGetOutputPath() {
        String path = dtoGenerator.getOutputPath(sampleEntity, config);
        assertEquals("com/example/domain/dto/UserDTO.java", path);
    }
}