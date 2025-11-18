package com.example.workflow.handlers;

import org.springframework.stereotype.Component;

/**
 * Handler for Terraform validation stage.
 * Simulates 'terraform validate' command.
 */
@Component
public class TerraformValidateHandler extends TerraformHandler {
    
    @Override
    protected java.util.Set<String> getRequiredAttributes() {
        // Validate requires terraform to be initialized
        return requireAttributes("terraform_initialized");
    }
    
    @Override
    protected boolean doHandle(com.example.workflow.context.TerraformContext context) {
        System.out.println("\n[Stage 2/5] Terraform Validate");
        System.out.println("─────────────────────────────");
        System.out.println("Validating Terraform configuration files...");
        System.out.println("Checking syntax and consistency...");
        
        try {
            // Simulate validation process
            Thread.sleep(400);
            
            // Store validation results in context
            context.setAttribute("configuration_valid", true);
            
            System.out.println("✓ Configuration is valid!");
            
            // Pass to next handler
            return passToNext(context);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Validation stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

