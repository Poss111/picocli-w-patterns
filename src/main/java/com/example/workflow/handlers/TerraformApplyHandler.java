package com.example.workflow.handlers;

import org.springframework.stereotype.Component;

/**
 * Handler for Terraform apply stage.
 * Simulates 'terraform apply' command.
 */
@Component
public class TerraformApplyHandler extends TerraformHandler {
    
    @Override
    protected java.util.Set<String> getRequiredAttributes() {
        // Apply requires plan to be created
        return requireAttributes("plan_created");
    }
    
    @Override
    protected boolean doHandle(com.example.workflow.context.TerraformContext context) {
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
            
            // Store apply results in context
            context.setAttribute("apply_complete", true);
            context.setAttribute("instance_id", "i-1234567890abcdef0");
            context.setAttribute("security_group_id", "sg-0123456789abcdef0");
            
            Integer added = context.getAttribute("resources_to_add", Integer.class);
            Integer changed = context.getAttribute("resources_to_change", Integer.class);
            Integer destroyed = context.getAttribute("resources_to_destroy", Integer.class);
            
            System.out.println("✓ Apply complete! Resources: " + 
                             (added != null ? added : 0) + " added, " +
                             (changed != null ? changed : 0) + " changed, " +
                             (destroyed != null ? destroyed : 0) + " destroyed.");
            
            // Pass to next handler
            return passToNext(context);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Apply stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

