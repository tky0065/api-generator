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
                    <#if field.isPrimary>
                        <constraints primaryKey="true" nullable="false"/>
                    <#elseif field.isNullable>
                        <constraints nullable="false"/>
                    </#if>
                </column>
            </#list>
        </createTable>

        <#list foreignKeys as fk>
            <addForeignKeyConstraint
                    baseTableName="${entity.tableName}"
                    baseColumnNames="${fk.columnName}"
                    constraintName="fk_${entity.tableName}_${fk.targetEntity?lower_case}"
                    referencedTableName="${fk.targetEntity?lower_case}"
                    referencedColumnNames="id"/>
        </#list>

        <#list entity.fields as field>
            <#if field.isUnique>
                <addUniqueConstraint
                        tableName="${entity.tableName}"
                        columnNames="${field.columnName}"
                        constraintName="uc_${entity.tableName}_${field.name}"/>
            </#if>

            <#if field.indexed>
                <createIndex
                        tableName="${entity.tableName}"
                        indexName="idx_${field.columnName}">
                    <column name="${field.columnName}"/>
                </createIndex>
            </#if>
        </#list>
    </changeSet>
</databaseChangeLog>// common/src/main/resources/templates/liquibase-changelog.ftl
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
                    <#if field.isPrimary>
                        <constraints primaryKey="true" nullable="false"/>
                    <#elseif field.isNullable>
                        <constraints nullable="false"/>
                    </#if>
                </column>
            </#list>
        </createTable>

        <#list foreignKeys as fk>
            <addForeignKeyConstraint
                    baseTableName="${entity.tableName}"
                    baseColumnNames="${fk.columnName}"
                    constraintName="fk_${entity.tableName}_${fk.targetEntity?lower_case}"
                    referencedTableName="${fk.targetEntity?lower_case}"
                    referencedColumnNames="id"/>
        </#list>

        <#list entity.fields as field>
            <#if field.isUnique>
                <addUniqueConstraint
                        tableName="${entity.tableName}"
                        columnNames="${field.columnName}"
                        constraintName="uc_${entity.tableName}_${field.name}"/>
            </#if>

            <#if field.indexed>
                <createIndex
                        tableName="${entity.tableName}"
                        indexName="idx_${field.columnName}">
                    <column name="${field.columnName}"/>
                </createIndex>
            </#if>
        </#list>
    </changeSet>
</databaseChangeLog>