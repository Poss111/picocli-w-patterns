package com.example.workflow.handlers;

/**
 * Handler for Terraform plan stage.
 * Simulates 'terraform plan' command.
 */
public class TerraformPlanHandler extends TerraformHandler {
    
    @Override
    public boolean handle(String workflowName, String timeToRun) {
        System.out.println("\n[Stage 3/5] Terraform Plan");
        System.out.println("─────────────────────────────");
        System.out.println("Creating execution plan for: " + workflowName);
        System.out.println("Scheduled for: " + timeToRun);
        System.out.println("Analyzing resource changes...");
        
        try {
            // Simulate plan process
            Thread.sleep(600);
            System.out.println("Plan: 3 to add, 1 to change, 0 to destroy.");
            System.out.println("✓ Plan created successfully!");
            
            // Pass to next handler
            return passToNext(workflowName, timeToRun);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Plan stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

