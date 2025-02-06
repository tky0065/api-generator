
package com.apigenerator.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldModel {
    private String name;
    private String type;
    private boolean isPrimary;
    private boolean isNullable;
    private String columnName;
    private String comment;
    private String length;
    private RelationshipType relationshipType;
    private String targetEntity;
}

