package com.apigenerator.intellij.processor;

import com.apigenerator.generators.*;  // Import des générateurs du module common
import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.intellij.services.GeneratorService;
import com.apigenerator.models.EntityModel;
import com.apigenerator.intellij.notifications.NotificationHelper;
import com.apigenerator.intellij.utils.PsiUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.util.Computable;

import com.intellij.util.IncorrectOperationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenerateApiProcessor {
    private final Project project;
    private final PsiClass sourceClass;
    private final GeneratorConfig config;
    private final GeneratorService generatorService;

    // Les générateurs du module common
    private final DtoGenerator dtoGenerator = new DtoGenerator();
    private final MapperGenerator mapperGenerator = new MapperGenerator();
    private final RepositoryGenerator repositoryGenerator = new RepositoryGenerator();
    private final ServiceGenerator serviceGenerator = new ServiceGenerator();
    private final ControllerGenerator controllerGenerator = new ControllerGenerator();
    private final LiquibaseGenerator liquibaseGenerator = new LiquibaseGenerator();



    public void generate() {
        try {
            ApplicationManager.getApplication().runWriteAction((Runnable) () -> {
                EntityModel entityModel = generatorService.createEntityModel(sourceClass);
                PsiDirectory baseDir = sourceClass.getContainingFile().getContainingDirectory();
                try {
                    generateApiFiles(entityModel, baseDir);
                } catch (IncorrectOperationException e) {
                    throw new RuntimeException(e);
                }
                NotificationHelper.notifyInfo(project, "API Generation",
                        "API generated successfully for " + sourceClass.getName());
            });
        } catch (Exception e) {
            NotificationHelper.notifyError(project, "API Generation Failed",
                    "Error generating API: " + e.getMessage());
        }
    }
    private void generateApiFiles(EntityModel entityModel, PsiDirectory baseDir) throws IncorrectOperationException {
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

    private void generateDto(EntityModel entityModel, PsiDirectory baseDir) throws IncorrectOperationException {
        String dtoPackage = entityModel.getPackageName() + ".dto";
        PsiUtils.createPackageIfNotExists(dtoPackage, baseDir);
        createJavaFile(dtoGenerator.generate(entityModel, config),
                dtoGenerator.getOutputPath(entityModel, config),
                baseDir);
    }

    private void generateMapper(EntityModel entityModel, PsiDirectory baseDir) throws IncorrectOperationException {
        String mapperPackage = entityModel.getPackageName() + ".mapper";
        PsiUtils.createPackageIfNotExists(mapperPackage, baseDir);
        createJavaFile(mapperGenerator.generate(entityModel, config),
                mapperGenerator.getOutputPath(entityModel, config),
                baseDir);
    }

    private void generateRepository(EntityModel entityModel, PsiDirectory baseDir) throws IncorrectOperationException {
        String repoPackage = entityModel.getPackageName() + ".repository";
        PsiUtils.createPackageIfNotExists(repoPackage, baseDir);
        createJavaFile(repositoryGenerator.generate(entityModel, config),
                repositoryGenerator.getOutputPath(entityModel, config),
                baseDir);
    }

    private void generateService(EntityModel entityModel, PsiDirectory baseDir) throws IncorrectOperationException {
        String servicePackage = entityModel.getPackageName() + ".service";
        PsiUtils.createPackageIfNotExists(servicePackage, baseDir);
        createJavaFile(serviceGenerator.generate(entityModel, config),
                serviceGenerator.getOutputPath(entityModel, config),
                baseDir);
    }

    private void generateController(EntityModel entityModel, PsiDirectory baseDir) throws IncorrectOperationException {
        String controllerPackage = entityModel.getPackageName() + ".controller";
        PsiUtils.createPackageIfNotExists(controllerPackage, baseDir);
        createJavaFile(controllerGenerator.generate(entityModel, config),
                controllerGenerator.getOutputPath(entityModel, config),
                baseDir);
    }

    private void generateLiquibase(EntityModel entityModel, PsiDirectory baseDir) throws IncorrectOperationException {
        String liquibasePath = liquibaseGenerator.getOutputPath(entityModel, config);
        createResourceFile(liquibaseGenerator.generate(entityModel, config),
                liquibasePath,
                baseDir);
    }

    private void createJavaFile(String content, String path, PsiDirectory baseDir) throws IncorrectOperationException {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        PsiElementFactory factory = psiFacade.getElementFactory();
        PsiFile file = factory.createFileFromText(path, content);
        baseDir.add(file);
    }

    private void createResourceFile(String content, String path, PsiDirectory baseDir) throws IncorrectOperationException {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        PsiElementFactory factory = psiFacade.getElementFactory();
        PsiFile file = factory.createFileFromText(path, content);
        baseDir.add(file);
    }
}