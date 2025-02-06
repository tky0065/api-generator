
package ${entity.packageName}.dto;

<#if config.useLombok>
    import lombok.Data;
    import lombok.Builder;
</#if>

/**
* DTO for ${entity.className}
*/
<#if config.useLombok>
    @Data
    @Builder
</#if>
public class ${entity.className}${config.dtoSuffix} {

<#list entity.fields as field>
    <#if field.comment??>
        /**
        * ${field.comment}
        */
    </#if>
    private ${field.type} ${field.name};
</#list>

<#if !config.useLombok>
    // Getters et Setters
    <#list entity.fields as field>

        public ${field.type} get${field.name?cap_first}() {
        return ${field.name};
        }

        public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.${field.name} = ${field.name};
        }
    </#list>
</#if>
}