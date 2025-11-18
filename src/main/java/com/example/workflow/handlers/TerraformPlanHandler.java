package com.example.workflow.handlers;

import org.springframework.stereotype.Component;

/**
 * Handler for Terraform plan stage.
 * Simulates 'terraform plan' command.
 */
@Component
public class TerraformPlanHandler extends TerraformHandler {
    
    @Override
    protected java.util.Set<String> getRequiredAttributes() {
        // Plan requires configuration to be validated
        return requireAttributes("configuration_valid", "workspace_name");
    }
    
    @Override
    protected boolean doHandle(com.example.workflow.context.TerraformContext context) {
        System.out.println("\n[Stage 3/5] Terraform Plan");
        System.out.println("─────────────────────────────");
        String workspaceName = context.getAttribute("workspace_name", String.class);
        System.out.println("Creating execution plan for: " + context.getWorkflowName());
        System.out.println("Workspace: " + workspaceName);
        System.out.println("Scheduled for: " + context.getTimeToRun());
        System.out.println("Analyzing resource changes...");
        
        try {
            // Simulate plan process
            Thread.sleep(600);
            
            // Store plan results in context
            context.setAttribute("plan_created", true);
            context.setAttribute("resources_to_add", 3);
            context.setAttribute("resources_to_change", 1);
            context.setAttribute("resources_to_destroy", 0);
            
            System.out.println("Plan: 3 to add, 1 to change, 0 to destroy.");
            System.out.println("✓ Plan created successfully!");
            
            // Pass to next handler
            return passToNext(context);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Plan stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

