// common/src/main/java/com/apigenerator/generators/DtoGenerator.java
package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DtoGenerator implements CodeGenerator {

    private final Configuration freemarkerConfig;

    public DtoGenerator() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates"
        );
    }

    @Override
    @SneakyThrows
    public String generate(EntityModel entity, GeneratorConfig config) {
        log.info("Génération du DTO pour l'entité: {}", entity.getClassName());

        Template template = freemarkerConfig.getTemplate("dto.ftl");
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);
        model.put("config", config);

        StringWriter writer = new StringWriter();
        template.process(model, writer);

        return writer.toString();
    }

    @Override
    public String getOutputPath(EntityModel entity, GeneratorConfig config) {
        return String.format("%s/dto/%sDTO.java",
                entity.getPackageName().replace(".", "/"),
                entity.getClassName()
        );
    }
}