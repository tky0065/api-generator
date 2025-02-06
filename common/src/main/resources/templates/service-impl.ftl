// common/src/main/resources/templates/service-impl.ftl
package ${entity.packageName}.service.impl;

import ${entity.packageName}.domain.${entity.className};
import ${entity.packageName}.repository.${entity.className}Repository;
import ${entity.packageName}.service.${entity.className}Service;
import ${entity.packageName}.service.mapper.${entity.className}Mapper;
import ${entity.packageName}.service.dto.${entity.className}DTO;
import ${entity.packageName}.service.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* Service Implementation pour gérer les ${entity.className}.
*/
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ${entity.className}ServiceImpl implements ${entity.className}Service {

private final ${entity.className}Repository ${entity.className?uncap_first}Repository;
private final ${entity.className}Mapper ${entity.className?uncap_first}Mapper;

@Override
public ${entity.className}DTO save(${entity.className}DTO ${entity.className?uncap_first}DTO) {
log.debug("Request to save ${entity.className} : {}", ${entity.className?uncap_first}DTO);
${entity.className} ${entity.className?uncap_first} = ${entity.className?uncap_first}Mapper.toEntity(${entity.className?uncap_first}DTO);
${entity.className?uncap_first} = ${entity.className?uncap_first}Repository.save(${entity.className?uncap_first});
return ${entity.className?uncap_first}Mapper.toDto(${entity.className?uncap_first});
}

@Override
@Transactional(readOnly = true)
public Page<${entity.className}DTO> findAll(Pageable pageable) {
    log.debug("Request to get all ${entity.className}s");
    return ${entity.className?uncap_first}Repository.findAll(pageable)
    .map(${entity.className?uncap_first}Mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<${entity.className}DTO> findOne(${idField.type} id) {
        log.debug("Request to get ${entity.className} : {}", id);
        return ${entity.className?uncap_first}Repository.findById(id)
        .map(${entity.className?uncap_first}Mapper::toDto);
        }

        @Override
        public ${entity.className}DTO update(${entity.className}DTO ${entity.className?uncap_first}DTO) {
        log.debug("Request to update ${entity.className} : {}", ${entity.className?uncap_first}DTO);

        if (!${entity.className?uncap_first}Repository.existsById(${entity.className?uncap_first}DTO.getId())) {
        throw new EntityNotFoundException("${entity.className} not found with id: " + ${entity.className?uncap_first}DTO.getId());
        }

        ${entity.className} ${entity.className?uncap_first} = ${entity.className?uncap_first}Mapper.toEntity(${entity.className?uncap_first}DTO);
        ${entity.className?uncap_first} = ${entity.className?uncap_first}Repository.save(${entity.className?uncap_first});
        return ${entity.className?uncap_first}Mapper.toDto(${entity.className?uncap_first});
        }

        @Override
        public void delete(${idField.type} id) {
        log.debug("Request to delete ${entity.className} : {}", id);
        if (!${entity.className?uncap_first}Repository.existsById(id)) {
        throw new EntityNotFoundException("${entity.className} not found with id: " + id);
        }
        ${entity.className?uncap_first}Repository.deleteById(id);
        }

        <#list searchableFields as field>
        @Override
        @Transactional(readOnly = true)
        public List<${entity.className}DTO> findBy${field.name?cap_first}(${field.type} ${field.name}) {
            log.debug("Request to get ${entity.className}s by ${field.name} : {}", ${field.name});
            return ${entity.className?uncap_first}Repository.findBy${field.name?cap_first}(${field.name}).stream()
            .map(${entity.className?uncap_first}Mapper::toDto)
            .collect(Collectors.toList());
            }
            </#list>
        }