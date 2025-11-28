package com.example.workflow.template.exceptions;

/**
 * Exception thrown when Terraform validation fails.
 */
public class ValidationException extends TerraformWorkflowException {
    
    public ValidationException(String message) {
        super("VALIDATE", message, false);
    }
    
    public ValidationException(String message, Throwable cause) {
        super("VALIDATE", message, cause, false);
    }
}

