package com.example.workflow.template;

/**
 * Abstract template class for Terraform workflow execution.
 * Implements the Template Method design pattern.
 * 
 * The template method defines the skeleton of the algorithm (Terraform workflow),
 * with concrete subclasses implementing the specific steps.
 */
public abstract class TerraformWorkflowTemplate {
    
    protected String workflowName;
    protected String timeToRun;
    
    /**
     * Template method that defines the workflow skeleton.
     * This method cannot be overridden to ensure the workflow structure is maintained.
     * 
     * @param workflowName the name of the workflow
     * @param timeToRun the scheduled time to run
     * @return true if workflow completed successfully, false otherwise
     */
    public final boolean executeWorkflow(String workflowName, String timeToRun) {
        this.workflowName = workflowName;
        this.timeToRun = timeToRun;
        
        printHeader();
        
        boolean success = true;
        
        // Execute each stage in order - this is the template structure
        if (!init()) {
            return false;
        }
        
        if (!validate()) {
            return false;
        }
        
        if (!plan()) {
            return false;
        }
        
        if (!apply()) {
            return false;
        }
        
        if (!output()) {
            return false;
        }
        
        printFooter(success);
        return success;
    }
    
    /**
     * Hook method - can be overridden to customize header display.
     */
    protected void printHeader() {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Terraform Workflow Template");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Workflow Name: " + workflowName);
        System.out.println("Scheduled Time: " + timeToRun);
        System.out.println("═══════════════════════════════════════════════");
    }
    
    /**
     * Hook method - can be overridden to customize footer display.
     */
    protected void printFooter(boolean success) {
        System.out.println("\n═══════════════════════════════════════════════");
        if (success) {
            System.out.println("  ✓ Workflow completed successfully!");
        } else {
            System.out.println("  ✗ Workflow failed!");
        }
        System.out.println("═══════════════════════════════════════════════");
    }
    
    // Abstract methods - must be implemented by concrete classes
    
    /**
     * Terraform Init stage - must be implemented by subclasses.
     * 
     * @return true if init succeeded, false otherwise
     */
    protected abstract boolean init();
    
    /**
     * Terraform Validate stage - must be implemented by subclasses.
     * 
     * @return true if validation succeeded, false otherwise
     */
    protected abstract boolean validate();
    
    /**
     * Terraform Plan stage - must be implemented by subclasses.
     * 
     * @return true if planning succeeded, false otherwise
     */
    protected abstract boolean plan();
    
    /**
     * Terraform Apply stage - must be implemented by subclasses.
     * 
     * @return true if apply succeeded, false otherwise
     */
    protected abstract boolean apply();
    
    /**
     * Terraform Output stage - must be implemented by subclasses.
     * 
     * @return true if output retrieval succeeded, false otherwise
     */
    protected abstract boolean output();
}

