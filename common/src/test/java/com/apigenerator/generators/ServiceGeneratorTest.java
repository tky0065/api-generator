package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ServiceGeneratorTest {

    private ServiceGenerator serviceGenerator;
    private EntityModel sampleEntity;
    private GeneratorConfig config;

    @BeforeEach
    void setUp() {
        serviceGenerator = new ServiceGenerator();

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
                                .name("email")
                                .type("String")
                                .columnName("email")
                                .isNullable(false)
                                .build()
                ))
                .build();

        config = GeneratorConfig.builder()
                .useLombok(true)
                .build();
    }

    @Test
    void testGenerateService() {
        String generated = serviceGenerator.generate(sampleEntity, config);

        // Vérifier l'interface du service
        assertTrue(generated.contains("public interface UserService"));
        assertTrue(generated.contains("UserDTO save(UserDTO userDTO)"));
        assertTrue(generated.contains("Optional<UserDTO> findOne(Long id)"));
        assertTrue(generated.contains("Page<UserDTO> findAll(Pageable pageable)"));
        assertTrue(generated.contains("void delete(Long id)"));

        // Vérifier l'implémentation du service
        assertTrue(generated.contains("public class UserServiceImpl implements UserService"));
        assertTrue(generated.contains("@Service"));
        assertTrue(generated.contains("@Transactional"));
        assertTrue(generated.contains("private final UserRepository userRepository"));
        assertTrue(generated.contains("private final UserMapper userMapper"));

        // Vérifier la gestion des exceptions
        assertTrue(generated.contains("throw new EntityNotFoundException"));
    }

    @Test
    void testGenerateServiceWithSearchMethods() {
        String generated = serviceGenerator.generate(sampleEntity, config);

        // Vérifier les méthodes de recherche
        assertTrue(generated.contains("List<UserDTO> findByEmail(String email)"));
    }

    @Test
    void testGetOutputPath() {
        String path = serviceGenerator.getOutputPath(sampleEntity, config);
        assertEquals("com/example/domain/service/UserService.java", path);
    }
}