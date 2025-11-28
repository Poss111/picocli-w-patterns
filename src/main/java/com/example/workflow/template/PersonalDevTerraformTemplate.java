package com.example.workflow.template;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.template.exceptions.TerraformWorkflowException;
import org.springframework.stereotype.Component;

/**
 * Personal Developer workflow implementation of the Terraform Template Method.
 * 
 * Optimized for fast iteration and local development:
 * - Uses shared init() from base template (workspace naming + provider setup)
 * - Minimal validation checks
 * - Local state storage
 * - Simplified logging
 * - Quick execution
 * - No approval gates
 * - Single resource deployments
 * 
 * Perfect for developers working on their local machines testing infrastructure code.
 * 
 * Note: This class does NOT override init() - it uses the shared implementation
 * from TerraformWorkflowTemplate, demonstrating selective method override.
 */
@Component
public class PersonalDevTerraformTemplate extends TerraformWorkflowTemplate {
    
    @Override
    protected void printHeader() {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║   Personal Developer Terraform Workflow       ║");
        System.out.println("║   Fast • Simple • Local                       ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println("Workflow: " + workflowName);
        System.out.println("Time: " + timeToRun);
        System.out.println("Mode: Local Development");
        System.out.println("State: Local File (terraform.tfstate)");
        System.out.println("Init: Using shared implementation");
        System.out.println("═══════════════════════════════════════════════");
    }
    
    @Override
    protected boolean validate(TerraformContext context) {
        System.out.println("\n[2/5] ✓ Basic Validation");
        System.out.println("▸ Syntax check only (skip policy validation)");
        
        try {
            Thread.sleep(100);
            
            context.setAttribute("validation_mode", "basic");
            context.setAttribute("validation_passed", true);
            
            System.out.println("✓ Syntax valid");
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean plan(TerraformContext context) {
        System.out.println("\n[3/5] 📋 Generate Plan");
        System.out.println("▸ Planning changes (no approval required)");
        
        try {
            Thread.sleep(300);
            
            System.out.println("\nChanges:");
            System.out.println("  + 1 resource to create");
            System.out.println("  ~ 0 to modify");
            System.out.println("  - 0 to destroy");
            
            context.setAttribute("resources_to_add", 1);
            context.setAttribute("plan_created", true);
            context.setAttribute("requires_approval", false);
            
            System.out.println("✓ Plan ready (auto-approved for local dev)");
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean apply(TerraformContext context) {
        System.out.println("\n[4/5] ⚡ Fast Apply");
        System.out.println("▸ Deploying to local/dev environment");
        
        try {
            Thread.sleep(400);
            
            System.out.println("▸ aws_instance.dev_server: Creating...");
            Thread.sleep(200);
            System.out.println("  ✓ Created [id=i-dev-12345]");
            
            context.setAttribute("apply_completed", true);
            context.setAttribute("instance_id", "i-dev-12345");
            context.setAttribute("environment", "local-dev");
            
            System.out.println("✓ Applied in 0.6s");
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean output(TerraformContext context) {
        System.out.println("\n[5/5] 📤 Outputs");
        
        String instanceId = context.getAttribute("instance_id", String.class);
        
        System.out.println("\nResults:");
        System.out.println("  instance_id: " + instanceId);
        System.out.println("  endpoint: http://localhost:8080");
        System.out.println("  cost: ~$0.02/hour (dev tier)");
        
        context.setAttribute("outputs_retrieved", true);
        
        System.out.println("\n✓ Ready for testing!");
        System.out.println("  💡 Tip: Run 'terraform destroy' when done");
        return true;
    }
    
    @Override
    protected void printFooter(boolean success) {
        System.out.println("\n═══════════════════════════════════════════════");
        if (success) {
            System.out.println("✓ Personal Dev Workflow Complete!");
            System.out.println("  Total time: ~1 second");
            System.out.println("  State: terraform.tfstate (local)");
        } else {
            System.out.println("✗ Workflow Failed");
            System.out.println("  Check terraform logs for details");
        }
        System.out.println("═══════════════════════════════════════════════");
    }
    
    @Override
    protected void onStageError(TerraformWorkflowException exception, int attemptNumber) {
        System.err.println("\n⚠ Quick retry (attempt " + attemptNumber + ")...");
    }
    
    @Override
    protected void cleanup(TerraformWorkflowException exception) {
        System.err.println("\n[Cleanup] Resetting local state...");
        System.err.println("  You can safely re-run the workflow");
    }
}

