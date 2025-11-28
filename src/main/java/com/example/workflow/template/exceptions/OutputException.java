package com.example.workflow.template.exceptions;

/**
 * Exception thrown when Terraform output retrieval fails.
 */
public class OutputException extends TerraformWorkflowException {
    
    public OutputException(String message) {
        super("OUTPUT", message, true);
    }
    
    public OutputException(String message, Throwable cause) {
        super("OUTPUT", message, cause, true);
    }
}

