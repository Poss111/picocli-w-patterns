package com.example.workflow.handlers;

import org.springframework.stereotype.Component;

/**
 * Handler for Terraform validation stage.
 * Simulates 'terraform validate' command.
 */
@Component
public class TerraformValidateHandler extends TerraformHandler {
    
    @Override
    public boolean handle(String workflowName, String timeToRun) {
        System.out.println("\n[Stage 2/5] Terraform Validate");
        System.out.println("─────────────────────────────");
        System.out.println("Validating Terraform configuration files...");
        System.out.println("Checking syntax and consistency...");
        
        try {
            // Simulate validation process
            Thread.sleep(400);
            System.out.println("✓ Configuration is valid!");
            
            // Pass to next handler
            return passToNext(workflowName, timeToRun);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Validation stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

