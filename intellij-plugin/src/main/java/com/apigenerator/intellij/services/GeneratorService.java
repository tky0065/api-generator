package com.apigenerator.intellij.services;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.generators.*;  // Pour DtoGenerator, etc.
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import com.apigenerator.models.RelationshipType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GeneratorService {
    private final Project project;
    private final DtoGenerator dtoGenerator;
    private final MapperGenerator mapperGenerator;
    private final RepositoryGenerator repositoryGenerator;
    private final ServiceGenerator serviceGenerator;
    private final ControllerGenerator controllerGenerator;
    private final LiquibaseGenerator liquibaseGenerator;

    public GeneratorService(Project project) {
        this.project = project;
        this.dtoGenerator = new DtoGenerator();
        this.mapperGenerator = new MapperGenerator();
        this.repositoryGenerator = new RepositoryGenerator();
        this.serviceGenerator = new ServiceGenerator();
        this.controllerGenerator = new ControllerGenerator();
        this.liquibaseGenerator = new LiquibaseGenerator();
    }
    public EntityModel createEntityModel(@NotNull PsiClass psiClass) {
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
    public void generateApi(PsiClass psiClass, GeneratorConfig config) {
        EntityModel entityModel = createEntityModel(psiClass);
        generateFiles(entityModel, config);
    }


    private String extractTableName(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getModifierList().getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if ("javax.persistence.Table".equals(annotation.getQualifiedName())) {
                PsiAnnotationMemberValue nameValue = annotation.findDeclaredAttributeValue("name");
                if (nameValue != null) {
                    return nameValue.getText().replace("\"", "");
                }
            }
        }
        return psiClass.getName().toLowerCase();
    }


    private FieldModel createFieldModel(PsiField psiField) {
        return FieldModel.builder()
                .name(psiField.getName())
                .type(psiField.getType().getPresentableText())
                .isPrimary(hasAnnotation(psiField, "javax.persistence.Id"))
                .isNullable(!hasAnnotation(psiField, "javax.validation.constraints.NotNull"))
                .columnName(extractColumnName(psiField))
                .relationshipType(extractRelationshipType(psiField))
                .targetEntity(extractTargetEntity(psiField))
                .isUnique(hasUniqueAnnotation(psiField))
                .indexed(hasAnnotation(psiField, "javax.persistence.Index"))
                .build();
    }
    private boolean hasAnnotation(PsiModifierListOwner element, String annotationName) {
        PsiModifierList modifierList = element.getModifierList();
        if (modifierList != null) {
            for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                if (annotationName.equals(annotation.getQualifiedName())) {
                    return true;
                }
            }
        }
        return false;
    }



    private String extractColumnName(PsiField field) {
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList != null) {
            for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                if ("javax.persistence.Column".equals(annotation.getQualifiedName())) {
                    PsiAnnotationMemberValue nameValue = annotation.findDeclaredAttributeValue("name");
                    if (nameValue != null) {
                        return nameValue.getText().replace("\"", "");
                    }
                }
            }
        }
        return field.getName();
    }

    private RelationshipType extractRelationshipType(PsiField field) {
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList != null) {
            for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                String qualifiedName = annotation.getQualifiedName();
                if (qualifiedName != null) {
                    switch (qualifiedName) {
                        case "javax.persistence.OneToOne":
                            return RelationshipType.ONE_TO_ONE;
                        case "javax.persistence.OneToMany":
                            return RelationshipType.ONE_TO_MANY;
                        case "javax.persistence.ManyToOne":
                            return RelationshipType.MANY_TO_ONE;
                        case "javax.persistence.ManyToMany":
                            return RelationshipType.MANY_TO_MANY;
                    }
                }
            }
        }
        return null;
    }

    private String extractTargetEntity(PsiField field) {
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList != null) {
            for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                String qualifiedName = annotation.getQualifiedName();
                if (qualifiedName != null && (
                        qualifiedName.equals("javax.persistence.OneToOne") ||
                                qualifiedName.equals("javax.persistence.OneToMany") ||
                                qualifiedName.equals("javax.persistence.ManyToOne") ||
                                qualifiedName.equals("javax.persistence.ManyToMany"))) {

                    PsiAnnotationMemberValue targetValue = annotation.findDeclaredAttributeValue("targetEntity");
                    if (targetValue != null) {
                        return targetValue.getText().replace(".class", "");
                    }
                }
            }
        }
        return null;
    }

    private boolean hasUniqueAnnotation(PsiField field) {
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList != null) {
            for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                if ("javax.persistence.Column".equals(annotation.getQualifiedName())) {
                    PsiAnnotationMemberValue uniqueValue = annotation.findDeclaredAttributeValue("unique");
                    return uniqueValue != null && Boolean.parseBoolean(uniqueValue.getText());
                }
            }
        }
        return false;
    }

    private boolean hasNotNullAnnotation(PsiField field) {
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList != null) {
            for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                String qualifiedName = annotation.getQualifiedName();
                if (qualifiedName != null && (
                        qualifiedName.equals("javax.validation.constraints.NotNull") ||
                                qualifiedName.equals("jakarta.validation.constraints.NotNull"))) {
                    return true;
                }
            }
        }
        return false;
    }



    private boolean hasLombokAnnotation(PsiClass psiClass) {
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList != null) {
            for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                String qualifiedName = annotation.getQualifiedName();
                if (qualifiedName != null && (
                        qualifiedName.equals("lombok.Data") ||
                                qualifiedName.equals("lombok.Getter") ||
                                qualifiedName.equals("lombok.Setter"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAuditingAnnotation(PsiClass psiClass) {
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList != null) {
            for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                if ("org.springframework.data.jpa.domain.support.AuditingEntityListener"
                        .equals(annotation.getQualifiedName())) {
                    return true;
                }
            }
        }
        return false;
    }


    private void generateFiles(EntityModel entityModel, GeneratorConfig config) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                generateDto(entityModel, config);
                generateMapper(entityModel, config);
                generateRepository(entityModel, config);
                generateService(entityModel, config);
                generateController(entityModel, config);
                if (config.isGenerateLiquibase()) {
                    generateLiquibase(entityModel, config);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate files", e);
            }
        });
    }
    private void generateDto(EntityModel entityModel, GeneratorConfig config) {
        String content = dtoGenerator.generate(entityModel, config);
        createFile(content, dtoGenerator.getOutputPath(entityModel, config));
    }
    private void generateMapper(EntityModel entityModel, GeneratorConfig config) {
        String mapperContent = mapperGenerator.generate(entityModel, config);
        createFile(mapperContent, mapperGenerator.getOutputPath(entityModel, config));
    }

    private void generateRepository(EntityModel entityModel, GeneratorConfig config) {
        String repoContent = repositoryGenerator.generate(entityModel, config);
        createFile(repoContent, repositoryGenerator.getOutputPath(entityModel, config));
    }

    private void generateService(EntityModel entityModel, GeneratorConfig config) {
        String serviceContent = serviceGenerator.generate(entityModel, config);
        createFile(serviceContent, serviceGenerator.getOutputPath(entityModel, config));
    }

    private void generateController(EntityModel entityModel, GeneratorConfig config) {
        String controllerContent = controllerGenerator.generate(entityModel, config);
        createFile(controllerContent, controllerGenerator.getOutputPath(entityModel, config));
    }

    private void generateLiquibase(EntityModel entityModel, GeneratorConfig config) {
        String liquibaseContent = liquibaseGenerator.generate(entityModel, config);
        createFile(liquibaseContent, liquibaseGenerator.getOutputPath(entityModel, config));
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