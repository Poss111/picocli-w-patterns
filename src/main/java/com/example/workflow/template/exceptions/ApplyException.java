package com.example.workflow.template.exceptions;

/**
 * Exception thrown when Terraform apply fails.
 */
public class ApplyException extends TerraformWorkflowException {
    
    public ApplyException(String message) {
        super("APPLY", message, false);
    }
    
    public ApplyException(String message, Throwable cause) {
        super("APPLY", message, cause, false);
    }
}

