package com.example.workflow.factory;

/**
 * Strategy interface for generating Terraform workspace names.
 * Part of the Strategy pattern integrated with Factory pattern.
 */
public interface WorkspaceNameStrategy {
    
    /**
     * Generates a workspace name based on the strategy implementation.
     * 
     * @param workflowName the base workflow name
     * @param timeToRun the scheduled time
     * @return the generated workspace name
     */
    String generateWorkspaceName(String workflowName, String timeToRun);
    
    /**
     * Returns a description of how this strategy generates workspace names.
     * 
     * @return strategy description
     */
    String getStrategyDescription();
}

