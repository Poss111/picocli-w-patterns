package com.example.workflow.template;

import org.springframework.stereotype.Component;

/**
 * Concrete implementation of TerraformWorkflowTemplate for Workflow 2.
 * Implements each stage of the Terraform workflow with specific behavior.
 */
@Component
public class Workflow2TerraformTemplate extends TerraformWorkflowTemplate {
    
    @Override
    protected void printHeader() {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Workflow 2: Terraform Template Pattern");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Workflow Name: " + workflowName);
        System.out.println("Scheduled Time: " + timeToRun);
        System.out.println("Pattern: Template Method");
        System.out.println("═══════════════════════════════════════════════");
    }
    
    @Override
    protected boolean init() {
        System.out.println("\n[Stage 1/5] Terraform Init");
        System.out.println("─────────────────────────────");
        System.out.println("Initializing Terraform working directory...");
        System.out.println("Configuring backend...");
        System.out.println("Downloading required providers...");
        
        try {
            Thread.sleep(500);
            System.out.println("- provider registry.terraform.io/hashicorp/aws v5.0.0");
            System.out.println("- provider registry.terraform.io/hashicorp/random v3.5.0");
            System.out.println("✓ Terraform initialized successfully!");
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Init failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean validate() {
        System.out.println("\n[Stage 2/5] Terraform Validate");
        System.out.println("─────────────────────────────");
        System.out.println("Validating configuration syntax...");
        System.out.println("Checking for errors in .tf files...");
        
        try {
            Thread.sleep(400);
            System.out.println("✓ Configuration is syntactically valid!");
            System.out.println("✓ All resource references are correct!");
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Validation failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean plan() {
        System.out.println("\n[Stage 3/5] Terraform Plan");
        System.out.println("─────────────────────────────");
        System.out.println("Generating execution plan for: " + workflowName);
        System.out.println("Reading current state...");
        System.out.println("Calculating changes...");
        
        try {
            Thread.sleep(600);
            System.out.println("\nPlanned changes:");
            System.out.println("  + aws_vpc.main");
            System.out.println("  + aws_subnet.public");
            System.out.println("  + aws_internet_gateway.main");
            System.out.println("  ~ aws_route_table.public (update in-place)");
            System.out.println("\nPlan: 3 to add, 1 to change, 0 to destroy.");
            System.out.println("✓ Plan generated successfully!");
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Planning failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean apply() {
        System.out.println("\n[Stage 4/5] Terraform Apply");
        System.out.println("─────────────────────────────");
        System.out.println("Applying infrastructure changes...");
        System.out.println("This may take several minutes...");
        
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
            
            System.out.println("\n✓ Apply complete!");
            System.out.println("  Resources: 3 added, 1 changed, 0 destroyed.");
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Apply failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    protected boolean output() {
        System.out.println("\n[Stage 5/5] Terraform Output");
        System.out.println("─────────────────────────────");
        System.out.println("Reading output values from state...");
        
        try {
            Thread.sleep(300);
            System.out.println("\nOutputs:");
            System.out.println("  vpc_id = \"vpc-abc123\"");
            System.out.println("  subnet_id = \"subnet-def456\"");
            System.out.println("  internet_gateway_id = \"igw-ghi789\"");
            System.out.println("  route_table_id = \"rtb-jkl012\"");
            System.out.println("  vpc_cidr = \"10.0.0.0/16\"");
            System.out.println("\n✓ All outputs retrieved successfully!");
            System.out.println("  Workflow '" + workflowName + "' completed at: " + timeToRun);
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Output retrieval failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

