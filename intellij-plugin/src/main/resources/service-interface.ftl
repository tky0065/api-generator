// src/main/resources/templates/service-interface.ftl
package ${entity.packageName}.service;

import ${entity.packageName}.service.dto.${entity.className}DTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
<#if hasFileFields>
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.core.io.Resource;
</#if>

/**
* Service Interface pour gérer les ${entity.className}s.
*/
public interface ${entity.className}Service {

/**
* Sauvegarder un(e) ${entity.className}.
*/
${entity.className}DTO save(${entity.className}DTO ${entity.className?uncap_first}DTO);

/**
* Mettre à jour un(e) ${entity.className}.
*/
${entity.className}DTO update(${entity.className}DTO ${entity.className?uncap_first}DTO);

/**
* Obtenir tous les ${entity.className}s.
*/
Page<${entity.className}DTO> findAll(Pageable pageable);

    /**
    * Obtenir un(e) ${entity.className} par son id.
    */
    Optional<${entity.className}DTO> findOne(${idType} id);

        /**
        * Supprimer un(e) ${entity.className} par son id.
        */
        void delete(${idType} id);

        <#if hasFileFields>
        /**
        * Téléverser un fichier pour un(e) ${entity.className}.
        */
        ${entity.className}DTO uploadFile(${idType} id, MultipartFile file);

        /**
        * Télécharger un fichier d'un(e) ${entity.className}.
        */
        Optional<Resource> downloadFile(${idType} id);
            </#if>
            }