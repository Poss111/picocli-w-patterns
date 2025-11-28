package com.example.workflow.template.exceptions;

/**
 * Exception thrown when Terraform initialization fails.
 */
public class InitializationException extends TerraformWorkflowException {
    
    public InitializationException(String message) {
        super("INIT", message, true);
    }
    
    public InitializationException(String message, Throwable cause) {
        super("INIT", message, cause, true);
    }
}

