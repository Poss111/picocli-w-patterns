package com.example.workflow.factory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Strategy that generates workspace names with timestamp.
 * Format: {workflowName}-{timestamp}
 */
public class TimestampBasedStrategy implements WorkspaceNameStrategy {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    
    @Override
    public String generateWorkspaceName(String workflowName, String timeToRun) {
        // Convert workflow name to lowercase and replace spaces with hyphens
        String sanitizedName = workflowName.toLowerCase()
                                          .replaceAll("\\s+", "-")
                                          .replaceAll("[^a-z0-9-]", "");
        
        String timestamp = LocalDateTime.now().format(FORMATTER);
        
        return sanitizedName + "-" + timestamp;
    }
    
    @Override
    public String getStrategyDescription() {
        return "Timestamp-based strategy (format: workflow-YYYYMMDD-HHMMSS)";
    }
}

