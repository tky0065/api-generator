// common/src/main/resources/templates/service-interface.ftl
package ${entity.packageName}.service;

import ${entity.packageName}.domain.${entity.className};
import ${entity.packageName}.dto.${entity.className}DTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

/**
* Service Interface pour gérer les ${entity.className}.
*/
public interface ${entity.className}Service {

/**
* Sauvegarder un(e) ${entity.className}.
*
* @param ${entity.className?uncap_first}DTO l'entité à sauvegarder
* @return l'entité sauvegardée
*/
${entity.className}DTO save(${entity.className}DTO ${entity.className?uncap_first}DTO);

/**
* Mettre à jour un(e) ${entity.className}.
*
* @param ${entity.className?uncap_first}DTO l'entité à mettre à jour
* @return l'entité mise à jour
*/
${entity.className}DTO update(${entity.className}DTO ${entity.className?uncap_first}DTO);

/**
* Obtenir tous les ${entity.className}s.
*
* @param pageable les informations de pagination
* @return la liste des entités
*/
Page<${entity.className}DTO> findAll(Pageable pageable);

    /**
    * Obtenir un(e) ${entity.className} par son id.
    *
    * @param id l'id de l'entité
    * @return l'entité trouvée
    */
    Optional<${entity.className}DTO> findOne(${idField.type} id);

        /**
        * Supprimer un(e) ${entity.className} par son id.
        *
        * @param id l'id de l'entité à supprimer
        */
        void delete(${idField.type} id);

        <#list searchableFields as field>
        /**
        * Rechercher des ${entity.className}s par ${field.name}.
        *
        * @param ${field.name} le ${field.name} à rechercher
        * @return la liste des entités trouvées
        */
        List<${entity.className}DTO> findBy${field.name?cap_first}(${field.type} ${field.name});
            </#list>
}