package com.example.workflow.handlers;

import org.springframework.stereotype.Component;

/**
 * Handler for Terraform initialization stage.
 * Simulates 'terraform init' command.
 */
@Component
public class TerraformInitHandler extends TerraformHandler {
    
    @Override
    protected java.util.Set<String> getRequiredAttributes() {
        // Init requires workspace_name to be set
        return requireAttributes("workspace_name");
    }
    
    @Override
    protected boolean doHandle(com.example.workflow.context.TerraformContext context) {
        System.out.println("\n[Stage 1/5] Terraform Init");
        System.out.println("─────────────────────────────");
        String workspaceName = context.getAttribute("workspace_name", String.class);
        System.out.println("Workspace: " + workspaceName);
        System.out.println("Initializing Terraform working directory...");
        System.out.println("Downloading provider plugins...");
        
        try {
            // Simulate init process
            Thread.sleep(500);
            
            // Store init results in context
            context.setAttribute("terraform_initialized", true);
            context.setAttribute("provider_version", "aws v5.0.0");
            
            System.out.println("✓ Terraform has been successfully initialized!");
            
            // Pass to next handler
            return passToNext(context);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Init stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

