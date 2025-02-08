package com.apigenerator.intellij.processor;

import com.apigenerator.intellij.services.GeneratorService;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.intellij.notifications.NotificationHelper;
import com.apigenerator.intellij.utils.PsiUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenerateApiProcessor {
    private final Project project;
    private final PsiClass sourceClass;
    private final GeneratorConfig config;
    private final GeneratorService generatorService;

    public void generate() {
        try {
            WriteAction.run(() -> {
                EntityModel entityModel = generatorService.createEntityModel(sourceClass);
                PsiDirectory baseDir = sourceClass.getContainingFile().getContainingDirectory();
                generateApiFiles(entityModel, baseDir);
                NotificationHelper.notifyInfo(project, "API Generation",
                        "API generated successfully for " + sourceClass.getName());
            });
        } catch (Exception e) {
            NotificationHelper.notifyError(project, "API Generation Failed",
                    "Error generating API: " + e.getMessage());
        }
    }

    private void generateApiFiles(EntityModel entityModel, PsiDirectory baseDir) {
        if (config.isGenerateDtos()) {
            generateDto(entityModel, baseDir);
        }
        if (config.isGenerateMappers()) {
            generateMapper(entityModel, baseDir);
        }
        generateRepository(entityModel, baseDir);
        generateService(entityModel, baseDir);
        generateController(entityModel, baseDir);
        if (config.isGenerateLiquibase()) {
            generateLiquibase(entityModel, baseDir);
        }
    }

    private void generateDto(EntityModel entityModel, PsiDirectory baseDir) {
        String dtoPackage = entityModel.getPackageName() + ".dto";
        PsiUtils.createPackageIfNotExists(dtoPackage, baseDir);
        createJavaFile(DtoGenerator.generate(entityModel, config),
                DtoGenerator.getOutputPath(entityModel, config),
                baseDir);
    }

    private void generateMapper(EntityModel entityModel, PsiDirectory baseDir) {
        String mapperPackage = entityModel.getPackageName() + ".mapper";
        PsiUtils.createPackageIfNotExists(mapperPackage, baseDir);
        createJavaFile(MapperGenerator.generate(entityModel, config),
                MapperGenerator.getOutputPath(entityModel, config),
                baseDir);
    }

    private void generateRepository(EntityModel entityModel, PsiDirectory baseDir) {
        String repoPackage = entityModel.getPackageName() + ".repository";
        PsiUtils.createPackageIfNotExists(repoPackage, baseDir);
        createJavaFile(RepositoryGenerator.generate(entityModel, config),
                RepositoryGenerator.getOutputPath(entityModel, config),
                baseDir);
    }
    private void generateService(EntityModel entityModel, PsiDirectory baseDir) {
        String servicePackage = entityModel.getPackageName() + ".service";
        PsiUtils.createPackageIfNotExists(servicePackage, baseDir);
        createJavaFile(ServiceGenerator.generate(entityModel, config),
                ServiceGenerator.getOutputPath(entityModel, config),
                baseDir);
    }

    private void generateController(EntityModel entityModel, PsiDirectory baseDir) {
        String controllerPackage = entityModel.getPackageName() + ".controller";
        PsiUtils.createPackageIfNotExists(controllerPackage, baseDir);
        createJavaFile(ControllerGenerator.generate(entityModel, config),
                ControllerGenerator.getOutputPath(entityModel, config),
                baseDir);
    }

    private void generateLiquibase(EntityModel entityModel, PsiDirectory baseDir) {
        String liquibasePath = LiquibaseGenerator.getOutputPath(entityModel, config);
        createResourceFile(LiquibaseGenerator.generate(entityModel, config),
                liquibasePath,
                baseDir);
    }
    private void createJavaFile(String content, String path, PsiDirectory baseDir) {
        PsiFileFactory factory = PsiFileFactory.getInstance(project);
        PsiFile file = factory.createFileFromText(path, JavaFileType.INSTANCE, content);
        baseDir.add(file);
    }

    private void createResourceFile(String content, String path, PsiDirectory baseDir) {
        PsiFileFactory factory = PsiFileFactory.getInstance(project);
        PsiFile file = factory.createFileFromText(path, PlainTextFileType.INSTANCE, content);
        baseDir.add(file);
    }
}
