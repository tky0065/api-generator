// common/src/main/java/com/apigenerator/exceptions/ErrorType.java
package com.apigenerator.exceptions;

public enum ErrorType {
    PARSING_ERROR("Erreur d'analyse"),
    GENERATION_ERROR("Erreur de génération"),
    VALIDATION_ERROR("Erreur de validation"),
    TEMPLATE_ERROR("Erreur de template"),
    IO_ERROR("Erreur d'entrée/sortie"),
CONFIGURATION_ERROR("Erreur de configuration"), ENTITY_NOT_FOUND(" Entité non trouvée"), FILE_ERROR(" Erreur de fichier"), INTERNAL_ERROR(" Erreur interne"), SECURITY_ERROR(" Erreur de sécurité"), UNAUTHORIZED(" Non autorisé"), FORBIDDEN(" Interdit"), BAD_REQUEST(" Requête incorrecte"), NOT_FOUND(" Introuvable"), METHOD_NOT_ALLOWED(" Méthode non autorisée"), CONFLICT(" Conflit"), UNSUPPORTED_MEDIA_TYPE(" Type de média non supporté"), UNPROCESSABLE_ENTITY(" Entité non traitable"), TOO_MANY_REQUESTS(" Trop de requêtes"), INTERNAL_SERVER_ERROR(" Erreur interne du serveur");

    private final String description;

    ErrorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}