package com.example.workflow.template.exceptions;

/**
 * Exception thrown when Terraform planning fails.
 */
public class PlanningException extends TerraformWorkflowException {
    
    public PlanningException(String message) {
        super("PLAN", message, true);
    }
    
    public PlanningException(String message, Throwable cause) {
        super("PLAN", message, cause, true);
    }
}

