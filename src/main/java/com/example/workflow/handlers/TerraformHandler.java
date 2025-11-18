package com.example.workflow.handlers;

/**
 * Abstract base class for Terraform stage handlers in the Chain of Responsibility pattern.
 */
public abstract class TerraformHandler {
    
    protected TerraformHandler nextHandler;
    
    /**
     * Sets the next handler in the chain.
     * 
     * @param nextHandler the next handler to be called
     * @return the next handler (for fluent chaining)
     */
    public TerraformHandler setNext(TerraformHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }
    
    /**
     * Handles the Terraform stage execution.
     * 
     * @param workflowName the name of the workflow
     * @param timeToRun the scheduled time to run
     * @return true if the stage completed successfully, false otherwise
     */
    public abstract boolean handle(String workflowName, String timeToRun);
    
    /**
     * Passes the request to the next handler in the chain.
     * 
     * @param workflowName the name of the workflow
     * @param timeToRun the scheduled time to run
     * @return true if all remaining stages complete successfully, false otherwise
     */
    protected boolean passToNext(String workflowName, String timeToRun) {
        if (nextHandler != null) {
            return nextHandler.handle(workflowName, timeToRun);
        }
        return true;
    }
}

