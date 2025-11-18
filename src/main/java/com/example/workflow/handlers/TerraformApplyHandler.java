package com.example.workflow.handlers;

import org.springframework.stereotype.Component;

/**
 * Handler for Terraform apply stage.
 * Simulates 'terraform apply' command.
 */
@Component
public class TerraformApplyHandler extends TerraformHandler {
    
    @Override
    public boolean handle(String workflowName, String timeToRun) {
        System.out.println("\n[Stage 4/5] Terraform Apply");
        System.out.println("─────────────────────────────");
        System.out.println("Applying Terraform changes...");
        System.out.println("Creating resources...");
        
        try {
            // Simulate apply process
            Thread.sleep(800);
            System.out.println("aws_instance.web_server: Creating...");
            Thread.sleep(300);
            System.out.println("aws_instance.web_server: Creation complete");
            System.out.println("aws_security_group.allow_http: Creating...");
            Thread.sleep(300);
            System.out.println("aws_security_group.allow_http: Creation complete");
            System.out.println("✓ Apply complete! Resources: 3 added, 1 changed, 0 destroyed.");
            
            // Pass to next handler
            return passToNext(workflowName, timeToRun);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Apply stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

