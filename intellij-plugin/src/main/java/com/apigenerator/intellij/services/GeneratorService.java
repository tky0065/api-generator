// intellij-plugin/src/main/java/com/apigenerator/intellij/services/GeneratorService.java
package com.apigenerator.intellij.services;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import com.apigenerator.models.RelationshipType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GeneratorService {
    private final Project project;

    public GeneratorService(Project project) {
        this.project = project;
    }

    public void generateApi(PsiClass psiClass, GeneratorConfig config) {
        EntityModel entityModel = createEntityModel(psiClass);
        generateFiles(entityModel, config);
    }

    private EntityModel createEntityModel(@NotNull PsiClass psiClass) {
        String packageName = ((PsiJavaFile) psiClass.getContainingFile()).getPackageName();
        String className = psiClass.getName();
        String tableName = extractTableName(psiClass);

        List<FieldModel> fields = new ArrayList<>();
        for (PsiField psiField : psiClass.getAllFields()) {
            fields.add(createFieldModel(psiField));
        }

        return EntityModel.builder()
                .packageName(packageName)
                .className(className)
                .tableName(tableName)
                .fields(fields)
                .hasLombok(hasLombokAnnotation(psiClass))
                .hasAuditing(hasAuditingAnnotation(psiClass))
                .build();
    }

    private String extractTableName(PsiClass psiClass) {
        PsiAnnotation tableAnnotation = psiClass.getAnnotation("javax.persistence.Table");
        if (tableAnnotation != null) {
            PsiAnnotationMemberValue nameValue = tableAnnotation.findAttributeValue("name");
            if (nameValue != null) {
                return nameValue.getText().replace("\"", "");
            }
        }
        return psiClass.getName().toLowerCase();
    }

    private FieldModel createFieldModel(PsiField psiField) {
        return FieldModel.builder()
                .name(psiField.getName())
                .type(psiField.getType().getPresentableText())
                .isPrimary(hasIdAnnotation(psiField))
                .isNullable(!hasNotNullAnnotation(psiField))
                .columnName(extractColumnName(psiField))
                .relationshipType(extractRelationshipType(psiField))
                .targetEntity(extractTargetEntity(psiField))
                .isUnique(hasUniqueAnnotation(psiField))
                .indexed(hasIndexAnnotation(psiField))
                .build();
    }

    private boolean hasIdAnnotation(PsiField field) {
        return field.hasAnnotation("javax.persistence.Id") ||
                field.hasAnnotation("jakarta.persistence.Id");
    }

    private boolean hasNotNullAnnotation(PsiField field) {
        return field.hasAnnotation("javax.validation.constraints.NotNull") ||
                field.hasAnnotation("jakarta.validation.constraints.NotNull");
    }

    private String extractColumnName(PsiField field) {
        PsiAnnotation columnAnnotation = field.getAnnotation("javax.persistence.Column");
        if (columnAnnotation != null) {
            PsiAnnotationMemberValue nameValue = columnAnnotation.findAttributeValue("name");
            if (nameValue != null) {
                return nameValue.getText().replace("\"", "");
            }
        }
        return field.getName();
    }

    private RelationshipType extractRelationshipType(PsiField field) {
        if (field.hasAnnotation("javax.persistence.OneToOne")) {
            return RelationshipType.ONE_TO_ONE;
        }
        if (field.hasAnnotation("javax.persistence.OneToMany")) {
            return RelationshipType.ONE_TO_MANY;
        }
        if (field.hasAnnotation("javax.persistence.ManyToOne")) {
            return RelationshipType.MANY_TO_ONE;
        }
        if (field.hasAnnotation("javax.persistence.ManyToMany")) {
            return RelationshipType.MANY_TO_MANY;
        }
        return null;
    }

    private String extractTargetEntity(PsiField field) {
        for (String annotationName : List.of(
                "javax.persistence.OneToOne",
                "javax.persistence.OneToMany",
                "javax.persistence.ManyToOne",
                "javax.persistence.ManyToMany")) {
            PsiAnnotation annotation = field.getAnnotation(annotationName);
            if (annotation != null) {
                PsiAnnotationMemberValue targetValue = annotation.findAttributeValue("targetEntity");
                if (targetValue != null) {
                    return targetValue.getText().replace(".class", "");
                }
            }
        }
        return null;
    }

    private boolean hasUniqueAnnotation(PsiField field) {
        PsiAnnotation columnAnnotation = field.getAnnotation("javax.persistence.Column");
        if (columnAnnotation != null) {
            PsiAnnotationMemberValue uniqueValue = columnAnnotation.findAttributeValue("unique");
            return uniqueValue != null && Boolean.parseBoolean(uniqueValue.getText());
        }
        return false;
    }

    private boolean hasIndexAnnotation(PsiField field) {
        return field.hasAnnotation("javax.persistence.Index");
    }

    private boolean hasLombokAnnotation(PsiClass psiClass) {
        return psiClass.hasAnnotation("lombok.Data") ||
                psiClass.hasAnnotation("lombok.Getter") ||
                psiClass.hasAnnotation("lombok.Setter");
    }

    private boolean hasAuditingAnnotation(PsiClass psiClass) {
        return psiClass.hasAnnotation("org.springframework.data.jpa.domain.support.AuditingEntityListener");
    }

    private void generateFiles(EntityModel entityModel, GeneratorConfig config) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            generateDto(entityModel, config);
            generateMapper(entityModel, config);
            generateRepository(entityModel, config);
            generateService(entityModel, config);
            generateController(entityModel, config);
            if (config.isGenerateLiquibase()) {
                generateLiquibase(entityModel, config);
            }
        });
    }

    private void generateDto(EntityModel entityModel, GeneratorConfig config) {
        String dtoContent = DtoGenerator.generate(entityModel, config);
        createFile(dtoContent, DtoGenerator.getOutputPath(entityModel, config));
    }

    private void generateMapper(EntityModel entityModel, GeneratorConfig config) {
        String mapperContent = MapperGenerator.generate(entityModel, config);
        createFile(mapperContent, MapperGenerator.getOutputPath(entityModel, config));
    }

    private void generateRepository(EntityModel entityModel, GeneratorConfig config) {
        String repoContent = RepositoryGenerator.generate(entityModel, config);
        createFile(repoContent, RepositoryGenerator.getOutputPath(entityModel, config));
    }

    private void generateService(EntityModel entityModel, GeneratorConfig config) {
        String serviceContent = ServiceGenerator.generate(entityModel, config);
        createFile(serviceContent, ServiceGenerator.getOutputPath(entityModel, config));
    }

    private void generateController(EntityModel entityModel, GeneratorConfig config) {
        String controllerContent = ControllerGenerator.generate(entityModel, config);
        createFile(controllerContent, ControllerGenerator.getOutputPath(entityModel, config));
    }

    private void generateLiquibase(EntityModel entityModel, GeneratorConfig config) {
        String liquibaseContent = LiquibaseGenerator.generate(entityModel, config);
        createFile(liquibaseContent, LiquibaseGenerator.getOutputPath(entityModel, config));
    }

    private void createFile(String content, String path) {
        VirtualFile baseDir = project.getBaseDir();
        String[] pathParts = path.split("/");
        VirtualFile currentDir = baseDir;

        try {
            for (int i = 0; i < pathParts.length - 1; i++) {
                VirtualFile subDir = currentDir.findChild(pathParts[i]);
                if (subDir == null) {
                    subDir = currentDir.createChildDirectory(this, pathParts[i]);
                }
                currentDir = subDir;
            }

            VirtualFile file = currentDir.findChild(pathParts[pathParts.length - 1]);
            if (file == null) {
                file = currentDir.createChildData(this, pathParts[pathParts.length - 1]);
            }

            file.setBinaryContent(content.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: " + path, e);
        }
    }
}