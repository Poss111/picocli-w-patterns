package com.example.workflow.factory;

/**
 * Strategy that generates workspace names with a custom prefix.
 * Format: {prefix}-{workflowName}
 */
public class CustomPrefixStrategy implements WorkspaceNameStrategy {
    
    private final String prefix;
    
    public CustomPrefixStrategy(String prefix) {
        this.prefix = prefix;
    }
    
    @Override
    public String generateWorkspaceName(String workflowName, String timeToRun) {
        // Convert workflow name to lowercase and replace spaces with hyphens
        String sanitizedName = workflowName.toLowerCase()
                                          .replaceAll("\\s+", "-")
                                          .replaceAll("[^a-z0-9-]", "");
        
        return prefix.toLowerCase() + "-" + sanitizedName;
    }
    
    @Override
    public String getStrategyDescription() {
        return "Custom prefix strategy (format: " + prefix + "-workflow)";
    }
}

