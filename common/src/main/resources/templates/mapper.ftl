package ${entity.packageName}.service.mapper;

<#if config.useMapstruct>
    import org.mapstruct.*;
</#if>
import ${entity.packageName}.domain.${entity.className};
import ${entity.packageName}.service.dto.${entity.className}DTO;

<#if config.useMapstruct>
    @Mapper(componentModel = "spring"<#if relationFields?has_content>, uses = {
    <#list relationFields as field>
        ${field.targetEntity}Mapper.class<#if field_has_next>,</#if>
    </#list>
    }</#if>)
</#if>
public <#if config.useMapstruct>interface<#else>class</#if> ${entity.className}Mapper extends EntityMapper<${entity.className}DTO, ${entity.className}> {

<#if config.useMapstruct>
    @Mapping(target = "id", source = "id")
    <#list relationFields as field>
        <#if field.relationshipType == "MANY_TO_ONE" || field.relationshipType == "ONE_TO_ONE">
            @Mapping(target = "${field.name}", source = "${field.name}")
        <#else>
            @Mapping(target = "${field.name}", ignore = true)
        </#if>
    </#list>
    <#list fileFields as field>
        @Mapping(target = "${field.name}", ignore = true)
    </#list>
    ${entity.className}DTO toDto(${entity.className} entity);
</#if>
}