package com.example.workflow.factory;

/**
 * Strategy that generates simple workspace names from the workflow name.
 * Format: {workflowName} (sanitized)
 */
public class SimpleStrategy implements WorkspaceNameStrategy {
    
    @Override
    public String generateWorkspaceName(String workflowName, String timeToRun) {
        // Convert workflow name to lowercase and replace spaces with hyphens
        return workflowName.toLowerCase()
                          .replaceAll("\\s+", "-")
                          .replaceAll("[^a-z0-9-]", "");
    }
    
    @Override
    public String getStrategyDescription() {
        return "Simple strategy (format: workflow-name-sanitized)";
    }
}

