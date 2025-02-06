// common/src/main/java/com/apigenerator/exceptions/ErrorSubDetail.java

package com.apigenerator.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorSubDetail {
    private String field;
    private String message;
}