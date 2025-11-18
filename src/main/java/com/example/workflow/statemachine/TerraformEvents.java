package com.example.workflow.statemachine;

/**
 * Enum defining all possible events that trigger state transitions in the Terraform workflow.
 */
public enum TerraformEvents {
    /**
     * Start the workflow
     */
    START_WORKFLOW,
    
    /**
     * Workspace name generated successfully
     */
    WORKSPACE_NAME_GENERATED,
    
    /**
     * Terraform initialized successfully
     */
    INIT_COMPLETED,
    
    /**
     * Validation completed successfully
     */
    VALIDATE_COMPLETED,
    
    /**
     * Plan created successfully
     */
    PLAN_COMPLETED,
    
    /**
     * Apply completed successfully
     */
    APPLY_COMPLETED,
    
    /**
     * Output retrieved successfully
     */
    OUTPUT_COMPLETED,
    
    /**
     * An error occurred
     */
    ERROR_OCCURRED
}

