package com.example.workflow.statemachine;

/**
 * Enum defining all possible states in the Terraform workflow state machine.
 */
public enum TerraformStates {
    /**
     * Initial state - workflow not started
     */
    INITIAL,
    
    /**
     * Generating workspace name
     */
    WORKSPACE_NAME_GENERATION,
    
    /**
     * Initializing Terraform
     */
    TERRAFORM_INIT,
    
    /**
     * Validating Terraform configuration
     */
    TERRAFORM_VALIDATE,
    
    /**
     * Creating execution plan
     */
    TERRAFORM_PLAN,
    
    /**
     * Applying changes
     */
    TERRAFORM_APPLY,
    
    /**
     * Retrieving outputs
     */
    TERRAFORM_OUTPUT,
    
    /**
     * Final state - workflow completed successfully
     */
    COMPLETED,
    
    /**
     * Error state - workflow failed
     */
    ERROR
}

