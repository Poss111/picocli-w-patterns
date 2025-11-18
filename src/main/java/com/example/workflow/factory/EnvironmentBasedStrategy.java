package com.example.workflow.factory;

/**
 * Strategy that generates workspace names based on environment.
 * Format: {workflowName}-{environment}
 */
public class EnvironmentBasedStrategy implements WorkspaceNameStrategy {
    
    private final String environment;
    
    public EnvironmentBasedStrategy(String environment) {
        this.environment = environment;
    }
    
    @Override
    public String generateWorkspaceName(String workflowName, String timeToRun) {
        // Convert workflow name to lowercase and replace spaces with hyphens
        String sanitizedName = workflowName.toLowerCase()
                                          .replaceAll("\\s+", "-")
                                          .replaceAll("[^a-z0-9-]", "");
        
        return sanitizedName + "-" + environment.toLowerCase();
    }
    
    @Override
    public String getStrategyDescription() {
        return "Environment-based strategy (format: workflow-" + environment + ")";
    }
}

