// intellij-plugin/src/main/resources/templates/test/repository-test.ftl
package ${entity.packageName}.repository;

import ${entity.packageName}.domain.${entity.className};
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ${entity.className}RepositoryTest {

@Autowired
private ${entity.className}Repository repository;

@Test
void shouldSaveAndFind${entity.className}() {
// Given
${entity.className} entity = create${entity.className}();

// When
${entity.className} saved = repository.save(entity);

// Then
assertThat(saved.getId()).isNotNull();
Optional<${entity.className}> found = repository.findById(saved.getId());
assertThat(found).isPresent();
assertThat(found.get()).isEqualToComparingFieldByField(saved);
}

<#list searchMethods as method>
    @Test
    void should${method}() {
    // Given
    ${entity.className} entity = create${entity.className}();
    repository.save(entity);

    // When
    Optional<${entity.className}> found = repository.${method}(entity.get${method?replace("findBy", "")}());

    // Then
    assertThat(found).isPresent();
    assertThat(found.get()).isEqualToComparingFieldByField(entity);
    }
</#list>

private ${entity.className} create${entity.className}() {
${entity.className} entity = new ${entity.className}();
<#list testData as field, value>
    entity.set${field?cap_first}(${value});
</#list>
return entity;
}
}