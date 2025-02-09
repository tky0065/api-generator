// src/main/resources/templates/service-impl.ftl
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
<#if hasFileFields>
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.core.io.Resource;
    import org.springframework.core.io.UrlResource;
    import java.nio.file.Path;
    import java.nio.file.Files;
    import java.nio.file.Paths;
</#if>
import java.util.Optional;

/**
* Service Implementation pour gérer les ${entity.className}s.
*/
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ${entity.className}ServiceImpl implements ${entity.className}Service {

private final ${entity.className}Repository repository;
private final ${entity.className}Mapper mapper;
<#if hasFileFields>
    private final Path fileStorageLocation = Paths.get("uploads/${entity.className?lower_case}s");
</#if>

@Override
public ${entity.className}DTO save(${entity.className}DTO dto) {
log.debug("Request to save ${entity.className} : {}", dto);
${entity.className} entity = mapper.toEntity(dto);
entity = repository.save(entity);
return mapper.toDto(entity);
}

@Override
@Transactional(readOnly = true)
public Page<${entity.className}DTO> findAll(Pageable pageable) {
    log.debug("Request to get all ${entity.className}s");
    return repository.findAll(pageable)
    .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<${entity.className}DTO> findOne(${idType} id) {
        log.debug("Request to get ${entity.className} : {}", id);
        return repository.findById(id)
        .map(mapper::toDto);
        }

        @Override
        public ${entity.className}DTO update(${entity.className}DTO dto) {
        log.debug("Request to update ${entity.className} : {}", dto);

        if (!repository.existsById(dto.getId())) {
        throw new EntityNotFoundException("${entity.className} not found with id: " + dto.getId());
        }

        ${entity.className} entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        return mapper.toDto(entity);
        }

        @Override
        public void delete(${idType} id) {
        log.debug("Request to delete ${entity.className} : {}", id);
        if (!repository.existsById(id)) {
        throw new EntityNotFoundException("${entity.className} not found with id: " + id);
        }
        repository.deleteById(id);
        }

        <#if hasFileFields>
        @Override
        public ${entity.className}DTO uploadFile(${idType} id, MultipartFile file) {
        ${entity.className} entity = repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("${entity.className} not found with id: " + id));

        try {
        String fileName = storeFile(file);
        // Mettre à jour l'entité avec le chemin du fichier
        <#list entity.fields as field>
            <#if field.type == "byte[]" || field.type == "Blob" || field.type == "File">
                entity.set${field.name?cap_first}(fileName);
            </#if>
        </#list>
        entity = repository.save(entity);
        return mapper.toDto(entity);
        } catch (Exception e) {
        throw new RuntimeException("Could not store file", e);
        }
        }

        @Override
        public Optional<Resource> downloadFile(${idType} id) {
            try {
            ${entity.className} entity = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("${entity.className} not found with id: " + id));

            <#list entity.fields as field>
                <#if field.type == "byte[]" || field.type == "Blob" || field.type == "File">
                    String fileName = entity.get${field.name?cap_first}();
                    Path filePath = fileStorageLocation.resolve(fileName);
                    Resource resource = new UrlResource(filePath.toUri());

                    if (resource.exists()) {
                    return Optional.of(resource);
                    }
                </#if>
            </#list>

            return Optional.empty();
            } catch (Exception e) {
            throw new RuntimeException("Could not read file", e);
            }
            }

            private String storeFile(MultipartFile file) throws Exception {
            Files.createDirectories(fileStorageLocation);
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            return fileName;
            }
            </#if>
            }