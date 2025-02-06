
package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;

public interface CodeGenerator {
    String generate(EntityModel entity, GeneratorConfig config);
    String getOutputPath(EntityModel entity, GeneratorConfig config);
}