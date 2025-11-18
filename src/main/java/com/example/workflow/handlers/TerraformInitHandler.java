package com.example.workflow.handlers;

import org.springframework.stereotype.Component;

/**
 * Handler for Terraform initialization stage.
 * Simulates 'terraform init' command.
 */
@Component
public class TerraformInitHandler extends TerraformHandler {
    
    @Override
    public boolean handle(String workflowName, String timeToRun) {
        System.out.println("\n[Stage 1/5] Terraform Init");
        System.out.println("─────────────────────────────");
        System.out.println("Initializing Terraform working directory...");
        System.out.println("Downloading provider plugins...");
        
        try {
            // Simulate init process
            Thread.sleep(500);
            System.out.println("✓ Terraform has been successfully initialized!");
            
            // Pass to next handler
            return passToNext(workflowName, timeToRun);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Init stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

