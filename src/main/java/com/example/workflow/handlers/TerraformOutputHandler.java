package com.example.workflow.handlers;

/**
 * Handler for Terraform output stage.
 * Simulates 'terraform output' command and displays final results.
 */
public class TerraformOutputHandler extends TerraformHandler {
    
    @Override
    public boolean handle(String workflowName, String timeToRun) {
        System.out.println("\n[Stage 5/5] Terraform Output");
        System.out.println("─────────────────────────────");
        System.out.println("Retrieving output values...");
        
        try {
            // Simulate output retrieval
            Thread.sleep(300);
            System.out.println("\nOutputs:");
            System.out.println("  instance_id = \"i-1234567890abcdef0\"");
            System.out.println("  public_ip = \"54.123.45.67\"");
            System.out.println("  security_group_id = \"sg-0123456789abcdef0\"");
            System.out.println("\n✓ Workflow '" + workflowName + "' completed successfully!");
            System.out.println("  All Terraform stages executed at: " + timeToRun);
            
            // End of chain - no next handler
            return passToNext(workflowName, timeToRun);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Output stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

