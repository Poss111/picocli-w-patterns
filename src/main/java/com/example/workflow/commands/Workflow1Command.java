package com.example.workflow.commands;

import com.example.workflow.factory.WorkspaceNameFactory;
import com.example.workflow.handlers.*;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "workflow1",
    description = "Run workflow 1 - Terraform deployment pipeline with Factory pattern",
    mixinStandardHelpOptions = true
)
public class Workflow1Command implements Runnable {

    @Option(
        names = {"-n", "--name"},
        description = "Name of the workflow",
        required = true
    )
    private String workflowName;

    @Option(
        names = {"-t", "--time"},
        description = "Time to run the workflow (e.g., '10:30 AM' or 'now')",
        required = true
    )
    private String timeToRun;
    
    @Option(
        names = {"-s", "--strategy"},
        description = "Workspace naming strategy: ENVIRONMENT, TIMESTAMP, CUSTOM_PREFIX, SIMPLE (default: TIMESTAMP)",
        defaultValue = "TIMESTAMP"
    )
    private WorkspaceNameFactory.StrategyType strategyType;
    
    @Option(
        names = {"-p", "--parameter"},
        description = "Strategy parameter (e.g., environment name for ENVIRONMENT, prefix for CUSTOM_PREFIX)"
    )
    private String strategyParameter;

    @Override
    public void run() {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Workflow 1: Terraform Deployment Pipeline");
        System.out.println("  Chain of Responsibility + Factory Pattern");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Workflow Name: " + workflowName);
        System.out.println("Scheduled Time: " + timeToRun);
        System.out.println("Naming Strategy: " + strategyType);
        System.out.println("═══════════════════════════════════════════════");
        
        // Build the Chain of Responsibility with Factory-created handler
        // Use factory pattern to create workspace name handler
        TerraformHandler workspaceNameHandler = new WorkspaceNameHandler(strategyType, strategyParameter);
        TerraformHandler initHandler = new TerraformInitHandler();
        TerraformHandler validateHandler = new TerraformValidateHandler();
        TerraformHandler planHandler = new TerraformPlanHandler();
        TerraformHandler applyHandler = new TerraformApplyHandler();
        TerraformHandler outputHandler = new TerraformOutputHandler();
        
        // Chain the handlers together - workspace name generation comes first
        workspaceNameHandler.setNext(initHandler)
                           .setNext(validateHandler)
                           .setNext(planHandler)
                           .setNext(applyHandler)
                           .setNext(outputHandler);
        
        // Start the chain
        boolean success = workspaceNameHandler.handle(workflowName, timeToRun);
        
        System.out.println("\n═══════════════════════════════════════════════");
        if (success) {
            System.out.println("  ✓ Pipeline completed successfully!");
        } else {
            System.out.println("  ✗ Pipeline failed!");
            System.exit(1);
        }
        System.out.println("═══════════════════════════════════════════════");
    }
}

