// common/src/main/java/com/apigenerator/exceptions/ErrorDetails.java
package com.apigenerator.exceptions;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorDetails {
    private ErrorSeverity severity;
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;
    private String sourceLocation;
    private Integer lineNumber;
    private Integer columnNumber;
    private List<String> stackTrace;

    public ErrorDetails(ErrorSeverity severity, String message) {
        this.severity = severity;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.stackTrace = new ArrayList<>();
    }

    public ErrorDetails(ErrorSeverity severity, Throwable ex) {
        this(severity, ex.getMessage());
        this.debugMessage = ex.getLocalizedMessage();

        // Capture de la stack trace
        for (StackTraceElement element : ex.getStackTrace()) {
            this.stackTrace.add(element.toString());
        }
    }

    public void setLocation(String sourceLocation, int lineNumber, int columnNumber) {
        this.sourceLocation = sourceLocation;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
}