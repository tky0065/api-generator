package com.apigenerator.generators;

import com.apigenerator.exceptions.ErrorDetails;
import com.apigenerator.exceptions.ErrorSeverity;
import com.apigenerator.exceptions.ErrorType;
import lombok.Getter;

@Getter
public class GeneratorException extends RuntimeException {
    private final ErrorType errorType;
    private final ErrorDetails errorDetails;

    public GeneratorException(String message, ErrorType errorType) {
        this(message, errorType, new ErrorDetails(ErrorSeverity.ERROR, message));
    }

    public GeneratorException(String message, ErrorType errorType, ErrorDetails errorDetails) {
        super(message);
        this.errorType = errorType;
        this.errorDetails = errorDetails;
    }

    public GeneratorException(String message, ErrorType errorType, Throwable cause) {
        this(message, errorType, new ErrorDetails(ErrorSeverity.ERROR, cause));
        this.errorDetails.setDebugMessage(cause.getMessage());
    }
}