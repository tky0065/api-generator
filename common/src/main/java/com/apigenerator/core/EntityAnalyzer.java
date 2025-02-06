package com.apigenerator.core;

import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import com.apigenerator.models.RelationshipType;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EntityAnalyzer {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+(.*?);");
    private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+(\\w+)");
    private static final Pattern TABLE_PATTERN = Pattern.compile("@Table\\s*\\(\\s*name\\s*=\\s*\"([^\"]+)\"\\s*\\)");
    private static final Pattern FIELD_PATTERN = Pattern.compile("private\\s+(\\w+(?:<.*?>)?)\\s+(\\w+)\\s*;");
    private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+(.*?);");
    private static final Pattern COLUMN_PATTERN = Pattern.compile("@Column\\s*\\(\\s*name\\s*=\\s*\"([^\"]+)\"\\s*(?:,\\s*nullable\\s*=\\s*(true|false))?\\s*(?:,\\s*length\\s*=\\s*(\\d+))?\\s*\\)");
    private static final Pattern ID_PATTERN = Pattern.compile("@Id");
    private static final Pattern RELATIONSHIP_PATTERN = Pattern.compile("@(OneToOne|OneToMany|ManyToOne|ManyToMany)(?:\\s*\\(\\s*targetEntity\\s*=\\s*(\\w+)\\.class\\s*\\))?");

    public EntityModel analyzeEntity(String sourceCode) {
        log.info("Début de l'analyse de l'entité");

        String packageName = extractPackage(sourceCode);
        String className = extractClassName(sourceCode);
        String tableName = extractTableName(sourceCode);
        List<String> imports = extractImports(sourceCode);
        List<FieldModel> fields = analyzeFields(sourceCode);

        // Détection de Lombok
        boolean hasLombok = imports.stream().anyMatch(i -> i.equals("lombok.Data"));

        // Détection de l'auditing
        boolean hasAuditing = imports.stream().anyMatch(i -> i.equals("org.springframework.data.jpa.domain.support.AuditingEntityListener"));

        return EntityModel.builder()
                .packageName(packageName)
                .className(className)
                .tableName(tableName)
                .fields(fields)
                .imports(imports)
                .hasLombok(hasLombok)
                .hasAuditing(hasAuditing)
                .build();
    }
    private String extractPackage(String sourceCode) {
        Matcher matcher = PACKAGE_PATTERN.matcher(sourceCode);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractClassName(String sourceCode) {
        Matcher matcher = CLASS_PATTERN.matcher(sourceCode);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractTableName(String sourceCode) {
        Matcher matcher = TABLE_PATTERN.matcher(sourceCode);
        return matcher.find() ? matcher.group(1) : null; // Retourner null si non trouvé
    }
    private List<String> extractImports(String sourceCode) {
        List<String> imports = new ArrayList<>();
        Matcher matcher = IMPORT_PATTERN.matcher(sourceCode);
        while (matcher.find()) {
            imports.add(matcher.group(1).trim());
        }
        return imports;
    }

    private List<FieldModel> analyzeFields(String sourceCode) {
        List<FieldModel> fields = new ArrayList<>();
        String[] lines = sourceCode.split("\n");

        FieldModel.FieldModelBuilder currentField = null;
        boolean isPrimaryKey = false;
        String columnName = null;
        boolean isNullable = true;
        String length = null;
        RelationshipType relationshipType = RelationshipType.NONE;
        String targetEntity = null;

        for (String line : lines) {
            line = line.trim();

            Matcher fieldMatcher = FIELD_PATTERN.matcher(line);
            if (fieldMatcher.find()) {
                if (currentField != null) {
                    fields.add(currentField.build());
                }

                currentField = FieldModel.builder()
                        .type(fieldMatcher.group(1))
                        .name(fieldMatcher.group(2))
                        .isPrimary(isPrimaryKey)
                        .isNullable(isNullable)
                        .columnName(columnName)
                        .length(length)
                        .relationshipType(relationshipType)
                        .targetEntity(targetEntity);

                isPrimaryKey = false;
                columnName = null;
                isNullable = true;
                length = null;
                relationshipType = RelationshipType.NONE;
                targetEntity = null;
                continue;
            }

            if (line.contains("@Id")) {
                isPrimaryKey = true;
            }

            Matcher columnMatcher = COLUMN_PATTERN.matcher(line);
            if (columnMatcher.find()) {
                columnName = columnMatcher.group(1);
                if (columnMatcher.group(2) != null) {
                    isNullable = Boolean.parseBoolean(columnMatcher.group(2));
                }
                if (columnMatcher.group(3) != null) {
                    length = columnMatcher.group(3);
                }
            }

            // Analyser les relations
            Matcher relationshipMatcher = RELATIONSHIP_PATTERN.matcher(line);
            if (relationshipMatcher.find()) {
                String relationType = relationshipMatcher.group(1);
                // Convertir "OneToOne" en "ONE_TO_ONE", etc.
                String enumValue = relationType.toUpperCase().replace("TO", "_TO_");
                relationshipType = RelationshipType.valueOf(enumValue);

                // Extraire l'entité cible si spécifiée
                Pattern targetPattern = Pattern.compile("targetEntity\\s*=\\s*(\\w+)\\.class");
                Matcher targetMatcher = targetPattern.matcher(line);
                if (targetMatcher.find()) {
                    targetEntity = targetMatcher.group(1);
                }
            }
        }

        if (currentField != null) {
            fields.add(currentField.build());
        }

        return fields;
    }
}
