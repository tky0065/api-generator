// common/src/test/resources/templates/liquibase-changelog.ftl
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.5.xsd">

    <changeSet id="${timestamp}" author="api-generator">
        <createTable tableName="${entity.tableName}">
            <#list entity.fields as field>
                <column name="${field.columnName}" type="${columnTypes[field.name]}">
                    <#if field.primary>
                        <constraints primaryKey="true" nullable="false"/>
                    <#elseif !field.nullable>
                        <constraints nullable="false"/>
                    </#if>
                </column>
            </#list>
        </createTable>

        <#if foreignKeys?has_content>
            <#list foreignKeys as fk>
                <addForeignKeyConstraint
                        baseTableName="${entity.tableName}"
                        baseColumnNames="${fk.columnName}"
                        constraintName="fk_${entity.tableName}_${fk.targetEntity?lower_case}"
                        referencedTableName="${fk.targetEntity?lower_case}"
                        referencedColumnNames="id"/>
            </#list>
        </#if>

        <#if uniqueConstraints?has_content>
            <#list uniqueConstraints as uc>
                <addUniqueConstraint
                        tableName="${entity.tableName}"
                        columnNames="${uc.columnName}"
                        constraintName="uc_${entity.tableName}_${uc.name}"/>
            </#list>
        </#if>

        <#if indices?has_content>
            <#list indices as idx>
                <createIndex
                        tableName="${entity.tableName}"
                        indexName="${idx}">
                    <column name="${idx?substring(4)}"/>
                </createIndex>
            </#list>
        </#if>
    </changeSet>
</databaseChangeLog>// common/src/test/resources/templates/liquibase-changelog.ftl
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.5.xsd">

    <changeSet id="${timestamp}" author="api-generator">
        <createTable tableName="${entity.tableName}">
            <#list entity.fields as field>
                <column name="${field.columnName}" type="${columnTypes[field.name]}">
                    <#if field.primary>
                        <constraints primaryKey="true" nullable="false"/>
                    <#elseif !field.nullable>
                        <constraints nullable="false"/>
                    </#if>
                </column>
            </#list>
        </createTable>

        <#if foreignKeys?has_content>
            <#list foreignKeys as fk>
                <addForeignKeyConstraint
                        baseTableName="${entity.tableName}"
                        baseColumnNames="${fk.columnName}"
                        constraintName="fk_${entity.tableName}_${fk.targetEntity?lower_case}"
                        referencedTableName="${fk.targetEntity?lower_case}"
                        referencedColumnNames="id"/>
            </#list>
        </#if>

        <#if uniqueConstraints?has_content>
            <#list uniqueConstraints as uc>
                <addUniqueConstraint
                        tableName="${entity.tableName}"
                        columnNames="${uc.columnName}"
                        constraintName="uc_${entity.tableName}_${uc.name}"/>
            </#list>
        </#if>

        <#if indices?has_content>
            <#list indices as idx>
                <createIndex
                        tableName="${entity.tableName}"
                        indexName="${idx}">
                    <column name="${idx?substring(4)}"/>
                </createIndex>
            </#list>
        </#if>
    </changeSet>
</databaseChangeLog>