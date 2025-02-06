package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import com.apigenerator.models.RelationshipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryGeneratorTest {

    private RepositoryGenerator repositoryGenerator;
    private EntityModel sampleEntity;
    private GeneratorConfig config;

    @BeforeEach
    void setUp() {
        repositoryGenerator = new RepositoryGenerator();

        // Configuration de base
        config = GeneratorConfig.builder().build();

        // Création d'une entité de test avec différents types de champs
        sampleEntity = EntityModel.builder()
                .packageName("com.example.domain")
                .className("User")
                .tableName("users")
                .fields(Arrays.asList(
                        // ID
                        FieldModel.builder()
                                .name("id")
                                .type("Long")
                                .isPrimary(true)
                                .build(),
                        // Champ simple
                        FieldModel.builder()
                                .name("email")
                                .type("String")
                                .build(),
                        // Champ avec relation
                        FieldModel.builder()
                                .name("department")
                                .type("Department")
                                .relationshipType(RelationshipType.MANY_TO_ONE)
                                .targetEntity("Department")
                                .build(),
                        // Autre champ simple
                        FieldModel.builder()
                                .name("active")
                                .type("Boolean")
                                .build()
                ))
                .build();
    }
    @Test
    void testGenerateBasicRepository() {
        String generated = repositoryGenerator.generate(sampleEntity, config);

        // Vérifier les éléments de base
        String[] expectedElements = {
                "public interface UserRepository",
                "extends JpaRepository<User, Long>",
                "JpaSpecificationExecutor<User>",
                "@Repository"
        };

        for (String element : expectedElements) {
            assertTrue(generated.contains(element),
                    "Le code devrait contenir: " + element);
        }
    }


    @Test
    void testGenerateSearchMethods() {
        String generated = repositoryGenerator.generate(sampleEntity, config);

        // Vérifier les méthodes de recherche générées
        String[] expectedMethods = {
                "Optional<User> findByEmail(String email)",
                "List<User> findByEmailIn(List<String> emailList)",
                "Optional<User> findByActive(Boolean active)",
                "List<User> findByActiveIn(List<Boolean> activeList)"
        };

        for (String method : expectedMethods) {
            assertTrue(generated.contains(method),
                    "Le repository devrait contenir la méthode: " + method);
        }
    }

    @Test
    void testNoSearchMethodsForRelations() {
        String generated = repositoryGenerator.generate(sampleEntity, config);

        // Vérifier qu'il n'y a pas de méthodes de recherche pour les relations
        assertFalse(generated.contains("findByDepartment"),
                "Ne devrait pas générer de méthode de recherche pour les relations");
    }

    @Test
    void testGenerateAdvancedSearchMethods() {
        String generated = repositoryGenerator.generate(sampleEntity, config);

        // Vérifier la présence des méthodes de recherche avancées
        assertTrue(generated.contains(
                        "Page<User> findAll(Specification<User> spec, Pageable pageable)"),
                "Devrait contenir la méthode de recherche avec spécification"
        );
    }

    @Test
    void testGetOutputPath() {
        String path = repositoryGenerator.getOutputPath(sampleEntity, config);
        assertEquals(
                "com/example/domain/repository/UserRepository.java",
                path,
                "Le chemin de sortie devrait être correct"
        );
    }

    @Test
    void testGenerateWithEmptyFields() {
        EntityModel emptyEntity = EntityModel.builder()
                .packageName("com.example.domain")
                .className("Empty")
                .fields(Arrays.asList(
                        FieldModel.builder()
                                .name("id")
                                .type("Long")
                                .isPrimary(true)
                                .build()
                ))
                .build();

        String generated = repositoryGenerator.generate(emptyEntity, config);

        // Vérifier que le repository est généré correctement même sans champs de recherche
        assertTrue(generated.contains("public interface EmptyRepository"));
        assertTrue(generated.contains("extends JpaRepository<Empty, Long>"));
        assertFalse(generated.contains("findBy"),
                "Ne devrait pas générer de méthodes de recherche pour une entité sans champs");
    }
}