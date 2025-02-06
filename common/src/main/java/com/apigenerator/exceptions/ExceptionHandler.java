package com.apigenerator.exceptions;


import com.apigenerator.generators.GeneratorException;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ExceptionHandler {

    public void handleException(GeneratorException ex) {
        log.error("Error occurred during generation: {} - Type: {}",
                ex.getMessage(),
                ex.getErrorType().getDescription());

        switch (ex.getErrorType()) {
            case ENTITY_NOT_FOUND -> handleEntityNotFound(ex);
            case PARSING_ERROR -> handleParsingError(ex);
            case VALIDATION_ERROR -> handleValidationError(ex);
            case GENERATION_ERROR -> handleGenerationError(ex);
            case FILE_ERROR -> handleFileError(ex);
            case TEMPLATE_ERROR -> handleTemplateError(ex);
        }
    }

    private void handleEntityNotFound(GeneratorException ex) {
        log.error("Entity not found: {}", ex.getMessage());
        // Notification spécifique pour l'IDE
    }

    private void handleParsingError(GeneratorException ex) {
        log.error("Error parsing source code: {}", ex.getMessage());
        // Notification spécifique pour l'IDE
    }

    private void handleValidationError(GeneratorException ex) {
        log.error("Validation error: {}", ex.getMessage());
        // Notification spécifique pour l'IDE
    }

    private void handleGenerationError(GeneratorException ex) {
        log.error("Code generation error: {}", ex.getMessage());
        // Notification spécifique pour l'IDE
    }

    private void handleFileError(GeneratorException ex) {
        log.error("File operation error: {}", ex.getMessage());
        // Notification spécifique pour l'IDE
    }

    private void handleTemplateError(GeneratorException ex) {
        log.error("Template processing error: {}", ex.getMessage());
        // Notification spécifique pour l'IDE
    }
}