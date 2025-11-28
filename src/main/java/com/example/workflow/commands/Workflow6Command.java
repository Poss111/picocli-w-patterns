package com.example.workflow.commands;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.template.EnterpriseTerraformTemplate;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "workflow6",
    description = "Enterprise Workflow - Production-grade with compliance, security, and audit trails",
    mixinStandardHelpOptions = true
)
public class Workflow6Command implements Runnable {

    private final EnterpriseTerraformTemplate template;

    @Option(
        names = {"-n", "--name"},
        description = "Name of the workflow",
        required = true
    )
    private String workflowName;

    @Option(
        names = {"-t", "--time"},
        description = "Scheduled time to run the workflow",
        defaultValue = "now"
    )
    private String timeToRun;
    
    @Option(
        names = {"-r", "--retries"},
        description = "Maximum number of retries (default: 3 for production reliability)",
        defaultValue = "3"
    )
    private int maxRetries;
    
    @Option(
        names = {"--environment"},
        description = "Target environment (production, staging)",
        defaultValue = "production"
    )
    private String environment;
    
    @Option(
        names = {"--region"},
        description = "AWS region for deployment",
        defaultValue = "us-east-1"
    )
    private String region;
    
    /**
     * Constructor with dependency injection.
     */
    public Workflow6Command(EnterpriseTerraformTemplate template) {
        this.template = template;
    }

    @Override
    public void run() {
        System.out.println("\n🏢 Starting Enterprise Workflow...\n");
        
        // Create context with enterprise metadata
        TerraformContext context = new TerraformContext(workflowName, timeToRun);
        context.setAttribute("environment", environment);
        context.setAttribute("region", region);
        context.setAttribute("enterprise_mode", true);
        context.setAttribute("compliance_required", true);
        context.setAttribute("audit_enabled", true);
        context.setAttribute("approval_required", true);
        
        System.out.println("Configuration:");
        System.out.println("  Environment: " + environment);
        System.out.println("  Region: " + region);
        System.out.println("  Compliance: ENABLED");
        System.out.println("  Audit Trail: ENABLED");
        System.out.println();
        
        // Configure for production reliability
        template.setMaxRetries(maxRetries);
        
        // Execute
        boolean success = template.executeWorkflow(workflowName, timeToRun, context);
        
        if (!success) {
            System.err.println("\n⚠  Enterprise deployment failed - see logs above");
            System.exit(1);
        }
    }
}

