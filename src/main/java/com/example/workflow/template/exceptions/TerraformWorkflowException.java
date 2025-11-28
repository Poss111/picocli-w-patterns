package com.example.workflow.template.exceptions;

/**
 * Base exception for Terraform workflow errors.
 * Provides context about which stage failed and additional details.
 */
public class TerraformWorkflowException extends Exception {
    
    private final String stage;
    private final boolean recoverable;
    
    /**
     * Creates a new TerraformWorkflowException.
     * 
     * @param stage the workflow stage where the error occurred
     * @param message the error message
     * @param recoverable whether the error is recoverable
     */
    public TerraformWorkflowException(String stage, String message, boolean recoverable) {
        super(message);
        this.stage = stage;
        this.recoverable = recoverable;
    }
    
    /**
     * Creates a new TerraformWorkflowException with a cause.
     * 
     * @param stage the workflow stage where the error occurred
     * @param message the error message
     * @param cause the underlying cause
     * @param recoverable whether the error is recoverable
     */
    public TerraformWorkflowException(String stage, String message, Throwable cause, boolean recoverable) {
        super(message, cause);
        this.stage = stage;
        this.recoverable = recoverable;
    }
    
    public String getStage() {
        return stage;
    }
    
    public boolean isRecoverable() {
        return recoverable;
    }
    
    @Override
    public String toString() {
        return String.format("TerraformWorkflowException{stage='%s', recoverable=%s, message='%s'}", 
                           stage, recoverable, getMessage());
    }
}

