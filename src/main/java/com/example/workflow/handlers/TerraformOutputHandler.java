package com.example.workflow.handlers;

import org.springframework.stereotype.Component;

/**
 * Handler for Terraform output stage.
 * Simulates 'terraform output' command and displays final results.
 */
@Component
public class TerraformOutputHandler extends TerraformHandler {
    
    @Override
    protected java.util.Set<String> getRequiredAttributes() {
        // Output requires apply to be complete
        return requireAttributes("apply_complete");
    }
    
    @Override
    protected boolean doHandle(com.example.workflow.context.TerraformContext context) {
        System.out.println("\n[Stage 5/5] Terraform Output");
        System.out.println("─────────────────────────────");
        System.out.println("Retrieving output values...");
        
        try {
            // Simulate output retrieval
            Thread.sleep(300);
            
            // Retrieve values from context
            String instanceId = context.getAttribute("instance_id", String.class);
            String securityGroupId = context.getAttribute("security_group_id", String.class);
            String workspaceName = context.getAttribute("workspace_name", String.class);
            
            System.out.println("\nOutputs:");
            System.out.println("  workspace_name = \"" + workspaceName + "\"");
            System.out.println("  instance_id = \"" + (instanceId != null ? instanceId : "unknown") + "\"");
            System.out.println("  public_ip = \"54.123.45.67\"");
            System.out.println("  security_group_id = \"" + (securityGroupId != null ? securityGroupId : "unknown") + "\"");
            System.out.println("\n✓ Workflow '" + context.getWorkflowName() + "' completed successfully!");
            System.out.println("  All Terraform stages executed at: " + context.getTimeToRun());
            System.out.println("  Total context attributes: " + context.getAttributeKeys().size());
            
            // End of chain - no next handler
            return passToNext(context);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Output stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

