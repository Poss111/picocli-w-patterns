package com.example.workflow.handlers;

import com.example.workflow.factory.WorkspaceNameFactory;
import com.example.workflow.factory.WorkspaceNameStrategy;

/**
 * Handler in the Chain of Responsibility that determines the Terraform workspace name.
 * Uses the Factory pattern to create different workspace naming strategies.
 */
public class WorkspaceNameHandler extends TerraformHandler {
    
    private final WorkspaceNameFactory.StrategyType strategyType;
    private final String strategyParameter;
    private String generatedWorkspaceName;
    
    /**
     * Creates a WorkspaceNameHandler with specified strategy type and parameter.
     * 
     * @param strategyType the type of naming strategy to use
     * @param strategyParameter optional parameter for the strategy (e.g., environment name)
     */
    public WorkspaceNameHandler(WorkspaceNameFactory.StrategyType strategyType, String strategyParameter) {
        this.strategyType = strategyType;
        this.strategyParameter = strategyParameter;
    }
    
    /**
     * Creates a WorkspaceNameHandler with specified strategy type (no parameter).
     * 
     * @param strategyType the type of naming strategy to use
     */
    public WorkspaceNameHandler(WorkspaceNameFactory.StrategyType strategyType) {
        this(strategyType, null);
    }
    
    @Override
    protected boolean doHandle(com.example.workflow.context.TerraformContext context) {
        System.out.println("\n[Stage 0/5] Workspace Name Generation");
        System.out.println("─────────────────────────────");
        System.out.println("Using Factory pattern to create workspace naming strategy...");
        
        try {
            // Use factory to create the appropriate strategy
            WorkspaceNameStrategy strategy = WorkspaceNameFactory.createStrategy(strategyType, strategyParameter);
            
            System.out.println("Strategy: " + strategy.getStrategyDescription());
            
            // Generate the workspace name using the strategy
            generatedWorkspaceName = strategy.generateWorkspaceName(context.getWorkflowName(), context.getTimeToRun());
            
            // Store workspace name in context for other handlers to use
            context.setAttribute("workspace_name", generatedWorkspaceName);
            
            System.out.println("Generated workspace name: " + generatedWorkspaceName);
            System.out.println("✓ Workspace name determined successfully!");
            System.out.println("  (stored in context as 'workspace_name')");
            
            // Pass to next handler
            return passToNext(context);
            
        } catch (IllegalArgumentException e) {
            System.err.println("✗ Workspace name generation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the generated workspace name.
     * 
     * @return the generated workspace name, or null if not yet generated
     */
    public String getGeneratedWorkspaceName() {
        return generatedWorkspaceName;
    }
}

