// src/main/resources/templates/repository.ftl
package ${entity.packageName}.repository;

import ${entity.packageName}.domain.${entity.className};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
* Repository pour gérer les entités ${entity.className}.
*/
@Repository
public interface ${entity.className}Repository extends JpaRepository<${entity.className}, <#list entity.fields as field><#if field.isPrimary>${field.type}</#if></#list>>,
JpaSpecificationExecutor<${entity.className}> {

<#list searchMethods as method>
    ${method};

</#list>
}