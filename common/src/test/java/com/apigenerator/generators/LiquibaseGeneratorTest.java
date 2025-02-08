package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import com.apigenerator.models.RelationshipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class LiquibaseGeneratorTest {

    private LiquibaseGenerator liquibaseGenerator;
    private EntityModel sampleEntity;
    private GeneratorConfig config;

    @BeforeEach
    void setUp() {
        liquibaseGenerator = new LiquibaseGenerator();

        config = GeneratorConfig.builder()
                .generateLiquibase(true)
                .build();

        sampleEntity = EntityModel.builder()
                .packageName("com.example.domain")
                .className("Product")
                .tableName("products")
                .fields(Arrays.asList(
                        // ID
                        FieldModel.builder()
                                .name("id")
                                .columnName("id")
                                .type("Long")
                                .isPrimary(true)
                                .build(),
                        // String avec longueur
                        FieldModel.builder()
                                .name("name")
                                .columnName("product_name")
                                .type("String")
                                .length("100")
                                .isNullable(false)
                                .isUnique(true)
                                .build(),
                        // Decimal
                        FieldModel.builder()
                                .name("price")
                                .columnName("price")
                                .type("BigDecimal")
                                .isNullable(false)
                                .build(),
                        // Relation
                        FieldModel.builder()
                                .name("category")
                                .columnName("category_id")
                                .type("Category")
                                .relationshipType(RelationshipType.MANY_TO_ONE)
                                .targetEntity("Category")
                                .build(),
                        // Boolean indexé
                        FieldModel.builder()
                                .name("active")
                                .columnName("is_active")
                                .type("Boolean")
                                .indexed(true)
                                .build()
                ))
                .build();
    }

    @Test
    void testGenerateBasicStructure() {
        String generated = liquibaseGenerator.generate(sampleEntity, config);

        // Vérifier la structure XML de base
        String[] expectedElements = {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<databaseChangeLog",
                "xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"",
                "<changeSet",
                "<createTable tableName=\"products\">"
        };

        for (String element : expectedElements) {
            assertTrue(generated.contains(element),
                    "Le changelog devrait contenir: " + element);
        }
    }

    @Test
    void testGenerateColumns() {
        String generated = liquibaseGenerator.generate(sampleEntity, config);

        // Vérifier les colonnes
        assertTrue(generated.contains("<column name=\"id\" type=\"bigint\">"));
        assertTrue(generated.contains("<column name=\"product_name\" type=\"varchar(100)\">"));
        assertTrue(generated.contains("<column name=\"price\" type=\"decimal(19,2)\">"));
        assertTrue(generated.contains("<column name=\"category_id\" type=\"bigint\">"));
        assertTrue(generated.contains("<column name=\"is_active\" type=\"boolean\">"));
    }

    @Test
    void testGenerateConstraints() {
        String generated = liquibaseGenerator.generate(sampleEntity, config);

        // Vérifier les contraintes
        assertTrue(generated.contains("<constraints primaryKey=\"true\""));
        assertTrue(generated.contains("<constraints nullable=\"false\""));
        assertTrue(generated.contains("addUniqueConstraint"));
        assertTrue(generated.contains("tableName=\"products\""));
        assertTrue(generated.contains("columnNames=\"product_name\""));
    }

    @Test
    void testGenerateForeignKeys() {
        String generated = liquibaseGenerator.generate(sampleEntity, config);

        // Vérifier les clés étrangères
        assertTrue(generated.contains("<addForeignKeyConstraint"));
        assertTrue(generated.contains("baseTableName=\"products\""));
        assertTrue(generated.contains("baseColumnNames=\"category_id\""));
        assertTrue(generated.contains("referencedTableName=\"category\""));
        assertTrue(generated.contains("referencedColumnNames=\"id\""));
    }

    @Test
    void testGenerateIndices() {
        String generated = liquibaseGenerator.generate(sampleEntity, config);
        System.out.println("Generated XML: " + generated);  // Pour le débogage

        // Vérifier les index
        assertTrue(generated.contains("<createIndex"),
                "Le XML devrait contenir la balise createIndex");
        assertTrue(generated.contains("indexName=\"idx_is_active\""),
                "Le XML devrait contenir l'index is_active");
        assertTrue(generated.contains("tableName=\"products\""),
                "Le XML devrait contenir le nom de la table products");
    }

    @Test
    void testGetOutputPath() {
        String path = liquibaseGenerator.getOutputPath(sampleEntity, config);

        assertTrue(path.matches("src/main/resources/db/changelog/changes/\\d{14}_create_products_table\\.xml"),
                "Le chemin du fichier devrait suivre le format attendu");
    }

    @Test
    void testEntityWithoutRelations() {
        EntityModel simpleEntity = EntityModel.builder()
                .packageName("com.example.domain")
                .className("Simple")
                .tableName("simple")
                .fields(Arrays.asList(
                        FieldModel.builder()
                                .name("id")
                                .columnName("id")
                                .type("Long")
                                .isPrimary(true)
                                .build(),
                        FieldModel.builder()
                                .name("name")
                                .columnName("name")
                                .type("String")
                                .build()
                ))
                .build();

        String generated = liquibaseGenerator.generate(simpleEntity, config);

        assertFalse(generated.contains("<addForeignKeyConstraint"),
                "Ne devrait pas générer de clés étrangères pour une entité sans relations");
    }
}