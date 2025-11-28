package com.example.workflow.template;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.template.exceptions.TerraformWorkflowException;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of TerraformWorkflowTemplate for Workflow 2.
 * Implements each stage of the Terraform workflow with specific behavior
 * and demonstrates error handling hooks.
 * Now includes context passing for shared state across stages.
 */
@Component
public class Workflow2TerraformTemplate extends TerraformWorkflowTemplate {
    
    private int errorCount = 0;
    
    // Failure simulation for testing
    private boolean failureSimulationEnabled = false;
    private String failureSimulationStage = "";
    private int failureSimulationAttempts = 999;
    private int currentAttempt = 0;
    
    @Override
    protected void printHeader() {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Workflow 2: Terraform Template Pattern");
        System.out.println("  (With Error Handling & Retry Logic)");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Workflow Name: " + workflowName);
        System.out.println("Scheduled Time: " + timeToRun);
        System.out.println("Pattern: Template Method");
        System.out.println("Max Retries: " + maxRetries);
        System.out.println("═══════════════════════════════════════════════");
    }
    
    @Override
    protected void onStageError(TerraformWorkflowException exception, int attemptNumber) {
        errorCount++;
        System.err.println("\n[Error Hook] Stage error detected:");
        System.err.println("  Stage: " + exception.getStage());
        System.err.println("  Attempt: " + attemptNumber + "/" + (maxRetries + 1));
        System.err.println("  Total errors so far: " + errorCount);
        
        // Custom recovery logic could go here
        // For example: clear cache, reset connections, etc.
    }
    
    @Override
    protected void cleanup(TerraformWorkflowException exception) {
        System.err.println("\n[Cleanup Hook] Performing workflow cleanup...");
        System.err.println("  Failed stage: " + exception.getStage());
        System.err.println("  Total errors encountered: " + errorCount);
        
        if (failureSimulationEnabled) {
            System.err.println("  (Note: This was a simulated failure for testing)");
        }
        
        // Rollback logic could go here
        // For example: terraform destroy resources created before failure
        System.err.println("  Checking for resources to clean up...");
        System.err.println("  Cleanup completed.");
    }
    
    /**
     * Enables failure simulation for testing error handling.
     * 
     * @param stage the stage to fail at (INIT, VALIDATE, PLAN, APPLY, OUTPUT)
     * @param attempts number of attempts before allowing success
     */
    public void enableFailureSimulation(String stage, int attempts) {
        this.failureSimulationEnabled = true;
        this.failureSimulationStage = stage.toUpperCase();
        this.failureSimulationAttempts = attempts;
        this.currentAttempt = 0;
    }
    
    /**
     * Checks if the current stage should fail for testing.
     * 
     * @param stageName the name of the current stage
     * @return true if should simulate failure, false otherwise
     */
    private boolean shouldSimulateFailure(String stageName) {
        if (!failureSimulationEnabled) {
            return false;
        }
        
        if (!stageName.equalsIgnoreCase(failureSimulationStage)) {
            return false;
        }
        
        currentAttempt++;
        boolean shouldFail = currentAttempt <= failureSimulationAttempts;
        
        if (shouldFail) {
            System.err.println("\n⚠ [TEST MODE] Simulating failure (attempt " + 
                             currentAttempt + "/" + failureSimulationAttempts + ")");
        } else {
            System.out.println("\n✓ [TEST MODE] Allowing stage to succeed after " + 
                             failureSimulationAttempts + " simulated failure(s)");
        }
        
        return shouldFail;
    }
    
    @Override
    protected boolean init(TerraformContext context) {
        System.out.println("\n[Stage 1/5] Terraform Init");
        System.out.println("─────────────────────────────");
        System.out.println("Initializing Terraform working directory...");
        System.out.println("Configuring backend...");
        System.out.println("Downloading required providers...");
        
        // Simulate failure if testing
        if (shouldSimulateFailure("INIT")) {
            throw new RuntimeException("Simulated init failure for testing");
        }
        
        try {
            Thread.sleep(500);
            System.out.println("- provider registry.terraform.io/hashicorp/aws v5.0.0");
            System.out.println("- provider registry.terraform.io/hashicorp/random v3.5.0");
            
            // Store initialization info in context
            context.setAttribute("providers_initialized", true);
            context.setAttribute("aws_provider_version", "5.0.0");
            context.setAttribute("backend_configured", true);
            
            System.out.println("✓ Terraform initialized successfully!");
            System.out.println("  [Context] Stored initialization metadata");
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Init failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean validate(TerraformContext context) {
        System.out.println("\n[Stage 2/5] Terraform Validate");
        System.out.println("─────────────────────────────");
        System.out.println("Validating configuration syntax...");
        System.out.println("Checking for errors in .tf files...");
        
        // Verify initialization was successful from context
        Boolean providersInitialized = context.getAttribute("providers_initialized", Boolean.class);
        if (providersInitialized == null || !providersInitialized) {
            System.err.println("✗ Validation prerequisite failed: Providers not initialized");
            return false;
        }
        System.out.println("  [Context] Verified providers initialized: " + providersInitialized);
        
        // Simulate failure if testing
        if (shouldSimulateFailure("VALIDATE")) {
            throw new RuntimeException("Simulated validation failure: Invalid resource reference");
        }
        
        try {
            Thread.sleep(400);
            System.out.println("✓ Configuration is syntactically valid!");
            System.out.println("✓ All resource references are correct!");
            
            // Store validation results in context
            context.setAttribute("validation_passed", true);
            context.setAttribute("resource_count", 4);
            
            System.out.println("  [Context] Stored validation results");
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Validation failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean plan(TerraformContext context) {
        System.out.println("\n[Stage 3/5] Terraform Plan");
        System.out.println("─────────────────────────────");
        System.out.println("Generating execution plan for: " + workflowName);
        System.out.println("Reading current state...");
        System.out.println("Calculating changes...");
        
        // Verify validation was successful from context
        Boolean validationPassed = context.getAttribute("validation_passed", Boolean.class);
        if (validationPassed == null || !validationPassed) {
            System.err.println("✗ Planning prerequisite failed: Validation not passed");
            return false;
        }
        Integer resourceCount = context.getAttribute("resource_count", Integer.class);
        System.out.println("  [Context] Validated resource count: " + resourceCount);
        
        // Simulate failure if testing
        if (shouldSimulateFailure("PLAN")) {
            throw new RuntimeException("Simulated plan failure: Unable to read remote state");
        }
        
        try {
            Thread.sleep(600);
            System.out.println("\nPlanned changes:");
            System.out.println("  + aws_vpc.main");
            System.out.println("  + aws_subnet.public");
            System.out.println("  + aws_internet_gateway.main");
            System.out.println("  ~ aws_route_table.public (update in-place)");
            System.out.println("\nPlan: 3 to add, 1 to change, 0 to destroy.");
            
            // Store plan results in context
            context.setAttribute("plan_generated", true);
            context.setAttribute("resources_to_add", 3);
            context.setAttribute("resources_to_change", 1);
            context.setAttribute("resources_to_destroy", 0);
            
            System.out.println("✓ Plan generated successfully!");
            System.out.println("  [Context] Stored plan metadata");
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Planning failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean apply(TerraformContext context) {
        System.out.println("\n[Stage 4/5] Terraform Apply");
        System.out.println("─────────────────────────────");
        System.out.println("Applying infrastructure changes...");
        System.out.println("This may take several minutes...");
        
        // Verify plan was generated from context
        Boolean planGenerated = context.getAttribute("plan_generated", Boolean.class);
        if (planGenerated == null || !planGenerated) {
            System.err.println("✗ Apply prerequisite failed: Plan not generated");
            return false;
        }
        Integer resourcesToAdd = context.getAttribute("resources_to_add", Integer.class);
        Integer resourcesToChange = context.getAttribute("resources_to_change", Integer.class);
        System.out.println("  [Context] Applying plan: " + resourcesToAdd + " to add, " + 
                         resourcesToChange + " to change");
        
        // Simulate failure if testing
        if (shouldSimulateFailure("APPLY")) {
            throw new RuntimeException("Simulated apply failure: Resource creation error");
        }
        
        try {
            System.out.println("\nCreating resources:");
            Thread.sleep(400);
            System.out.println("  aws_vpc.main: Creating...");
            Thread.sleep(300);
            System.out.println("  aws_vpc.main: Creation complete [id=vpc-abc123]");
            Thread.sleep(300);
            System.out.println("  aws_subnet.public: Creating...");
            Thread.sleep(300);
            System.out.println("  aws_subnet.public: Creation complete [id=subnet-def456]");
            Thread.sleep(300);
            System.out.println("  aws_internet_gateway.main: Creating...");
            Thread.sleep(300);
            System.out.println("  aws_internet_gateway.main: Creation complete [id=igw-ghi789]");
            Thread.sleep(200);
            System.out.println("  aws_route_table.public: Modifying...");
            Thread.sleep(200);
            System.out.println("  aws_route_table.public: Modifications complete [id=rtb-jkl012]");
            
            // Store apply results in context
            context.setAttribute("apply_completed", true);
            context.setAttribute("vpc_id", "vpc-abc123");
            context.setAttribute("subnet_id", "subnet-def456");
            context.setAttribute("igw_id", "igw-ghi789");
            context.setAttribute("rtb_id", "rtb-jkl012");
            
            System.out.println("\n✓ Apply complete!");
            System.out.println("  Resources: 3 added, 1 changed, 0 destroyed.");
            System.out.println("  [Context] Stored resource IDs");
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Apply failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean output(TerraformContext context) {
        System.out.println("\n[Stage 5/5] Terraform Output");
        System.out.println("─────────────────────────────");
        System.out.println("Reading output values from state...");
        
        // Verify apply was completed and retrieve resource IDs from context
        Boolean applyCompleted = context.getAttribute("apply_completed", Boolean.class);
        if (applyCompleted == null || !applyCompleted) {
            System.err.println("✗ Output prerequisite failed: Apply not completed");
            return false;
        }
        
        String vpcId = context.getAttribute("vpc_id", String.class);
        String subnetId = context.getAttribute("subnet_id", String.class);
        String igwId = context.getAttribute("igw_id", String.class);
        String rtbId = context.getAttribute("rtb_id", String.class);
        System.out.println("  [Context] Retrieved resource IDs from context");
        
        // Simulate failure if testing
        if (shouldSimulateFailure("OUTPUT")) {
            throw new RuntimeException("Simulated output failure: Unable to read state file");
        }
        
        try {
            Thread.sleep(300);
            System.out.println("\nOutputs:");
            System.out.println("  vpc_id = \"" + vpcId + "\"");
            System.out.println("  subnet_id = \"" + subnetId + "\"");
            System.out.println("  internet_gateway_id = \"" + igwId + "\"");
            System.out.println("  route_table_id = \"" + rtbId + "\"");
            System.out.println("  vpc_cidr = \"10.0.0.0/16\"");
            
            // Store output metadata in context
            context.setAttribute("outputs_retrieved", true);
            context.setAttribute("output_count", 5);
            
            System.out.println("\n✓ All outputs retrieved successfully!");
            System.out.println("  Workflow '" + workflowName + "' completed at: " + timeToRun);
            System.out.println("  [Context] Final attribute count: " + context.getAttributeKeys().size());
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Output retrieval failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

