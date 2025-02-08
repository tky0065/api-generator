package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class LiquibaseGenerator implements CodeGenerator {

    private final Configuration freemarkerConfig;
    private static final DateTimeFormatter CHANGELOG_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public LiquibaseGenerator() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates"
        );
    }

    @Override
    @SneakyThrows
    public String generate(EntityModel entity, GeneratorConfig config) {
        log.info("Génération du changelog Liquibase pour l'entité: {}", entity.getClassName());

        Template template = freemarkerConfig.getTemplate("liquibase-changelog.ftl");
        Map<String, Object> model = createTemplateModel(entity);

        StringWriter writer = new StringWriter();
        template.process(model, writer);

        return writer.toString();
    }

    @Override
    public String getOutputPath(EntityModel entity, GeneratorConfig config) {
        String timestamp = LocalDateTime.now().format(CHANGELOG_DATE_FORMAT);
        return String.format("src/main/resources/db/changelog/changes/%s_create_%s_table.xml",
                timestamp,
                entity.getTableName()
        );
    }

    private Map<String, Object> createTemplateModel(EntityModel entity) {
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);
        model.put("timestamp", LocalDateTime.now().format(CHANGELOG_DATE_FORMAT));
        model.put("columnTypes", getColumnTypes(entity.getFields()));
        model.put("primaryKey", findPrimaryKey(entity.getFields()));
        model.put("foreignKeys", findForeignKeys(entity.getFields()));
        model.put("uniqueConstraints", findUniqueConstraints(entity.getFields()));
        model.put("indices", generateIndices(entity.getFields()));
        return model;
    }

    private Map<String, String> getColumnTypes(List<FieldModel> fields) {
        Map<String, String> columnTypes = new HashMap<>();
        for (FieldModel field : fields) {
            columnTypes.put(field.getName(), mapJavaTypeToSqlType(field));
        }
        return columnTypes;
    }

    private String mapJavaTypeToSqlType(FieldModel field) {
        switch (field.getType()) {
            case "String":
                return field.getLength() != null ?
                        "varchar(" + field.getLength() + ")" : "varchar(255)";
            case "Integer":
                return "integer";
            case "Long":
                return "bigint";
            case "Boolean":
                return "boolean";
            case "LocalDateTime":
                return "timestamp";
            case "BigDecimal":
                return "decimal(19,2)";
            case "byte[]":
                return "bytea";
            default:
                if (field.getRelationshipType() != null) {
                    return "bigint"; // ID reference
                }
                return "varchar(255)";
        }
    }

    private FieldModel findPrimaryKey(List<FieldModel> fields) {
        return fields.stream()
                .filter(FieldModel::isPrimary)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No primary key found"));
    }

    private List<FieldModel> findForeignKeys(List<FieldModel> fields) {
        return fields.stream()
                .filter(f -> f.getRelationshipType() != null)
                .toList();
    }

    private List<FieldModel> findUniqueConstraints(List<FieldModel> fields) {
        return fields.stream()
                .filter(f -> Boolean.TRUE.equals(f.isUnique()))
                .toList();
    }
    private List<String> generateIndices(List<FieldModel> fields) {
        return fields.stream()
                .filter(FieldModel::isIndexed)
                .map(field -> String.format("idx_%s", field.getColumnName()))
                .collect(Collectors.toList());
    }
}