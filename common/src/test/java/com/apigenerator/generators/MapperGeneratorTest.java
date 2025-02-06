package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import com.apigenerator.models.RelationshipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MapperGeneratorTest {

    private MapperGenerator mapperGenerator;
    private GeneratorConfig config;
    private EntityModel sampleEntity;

    @BeforeEach
    void setUp() {
        mapperGenerator = new MapperGenerator();

        config = GeneratorConfig.builder()
                .useMapstruct(true)
                .build();

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
                                .name("name")
                                .type("String")
                                .build(),
                        FieldModel.builder()
                                .name("department")
                                .type("Department")
                                .relationshipType(RelationshipType.MANY_TO_ONE)
                                .targetEntity("Department")
                                .build(),
                        FieldModel.builder()
                                .name("avatar")
                                .type("byte[]")
                                .build()
                ))
                .build();
    }

    @Test
    void testGenerateBasicMapper() {
        String generated = mapperGenerator.generate(sampleEntity, config);
        assertNotNull(generated);
        assertFalse(generated.isEmpty());

        // Vérifier les éléments de base
        String[] expectedElements = {
                "public interface UserMapper",
                "extends EntityMapper<UserDTO, User>",
                "@Mapper(componentModel = \"spring\"",
                "package com.example.domain.service.mapper"
        };

        for (String element : expectedElements) {
            assertTrue(generated.contains(element),
                    "Le code généré devrait contenir: " + element);
        }
    }

    @Test
    void testGenerateMapperWithRelationships() {
        String generated = mapperGenerator.generate(sampleEntity, config);
        assertNotNull(generated);

        // Vérifier les mappings de relation
        String[] expectedElements = {
                "DepartmentMapper.class",
                "@Mapping(target = \"department\", source = \"department\")"
        };

        for (String element : expectedElements) {
            assertTrue(generated.contains(element),
                    "Le code généré devrait contenir: " + element);
        }
    }

    @Test
    void testMapperWithDisabledMapstruct() {
        config = GeneratorConfig.builder()
                .useMapstruct(false)
                .build();

        String generated = mapperGenerator.generate(sampleEntity, config);
        assertNotNull(generated);

        assertTrue(generated.contains("public class UserMapper"),
                "Devrait générer une classe plutôt qu'une interface");
        assertFalse(generated.contains("@Mapper"),
                "Ne devrait pas contenir d'annotations MapStruct");
    }

    @Test
    void testGetOutputPath() {
        String path = mapperGenerator.getOutputPath(sampleEntity, config);
        assertEquals(
                "com/example/domain/service/mapper/UserMapper.java",
                path
        );
    }
}