package com.apigenerator.models;

import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class EntityModel {
    private String packageName;
    private String className;
    private String tableName;
    private List<FieldModel> fields;
    private List<String> imports;
    private boolean hasLombok;
    private boolean hasAuditing;
    private String comment;
}