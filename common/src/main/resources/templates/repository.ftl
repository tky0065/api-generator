package ${entity.packageName}.repository;

import ${entity.packageName}.domain.${entity.className};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Optional;

@Repository
public interface ${entity.className}Repository extends JpaRepository<${entity.className}, ${primaryKeyType}>,
JpaSpecificationExecutor<${entity.className}> {

<#list searchMethods as method>
    ${method}

</#list>
}
