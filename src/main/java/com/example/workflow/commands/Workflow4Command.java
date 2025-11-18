package com.example.workflow.commands;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.factory.WorkspaceNameFactory;
import com.example.workflow.statemachine.TerraformEvents;
import com.example.workflow.statemachine.TerraformStates;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Workflow 4 Command - Demonstrates Spring State Machine pattern.
 * Replicates Workflow1's functionality using state-driven approach.
 */
@Component
@Command(
    name = "workflow4",
    description = "Run workflow 4 - Terraform deployment using Spring State Machine pattern",
    mixinStandardHelpOptions = true
)
public class Workflow4Command implements Runnable {
    
    private final StateMachineFactory<TerraformStates, TerraformEvents> stateMachineFactory;
    
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
    
    /**
     * Constructor with dependency injection.
     * Spring automatically injects the state machine factory.
     */
    public Workflow4Command(StateMachineFactory<TerraformStates, TerraformEvents> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
    }

    @Override
    public void run() {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Workflow 4: Terraform Deployment Pipeline");
        System.out.println("  Spring State Machine Pattern");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Workflow Name: " + workflowName);
        System.out.println("Scheduled Time: " + timeToRun);
        System.out.println("Naming Strategy: " + strategyType);
        System.out.println("═══════════════════════════════════════════════");
        
        // Create the context object
        TerraformContext context = new TerraformContext(workflowName, timeToRun);
        context.setAttribute("environment", strategyParameter != null ? strategyParameter : "default");
        context.setAttribute("strategy_type", strategyType.toString());
        
        System.out.println("Created context with initial attributes: " + context.getAttributeKeys());
        
        // Create a new state machine instance
        StateMachine<TerraformStates, TerraformEvents> stateMachine = stateMachineFactory.getStateMachine();
        
        // Store context and strategy in state machine extended state
        stateMachine.getExtendedState().getVariables().put("context", context);
        stateMachine.getExtendedState().getVariables().put("strategyType", strategyType);
        stateMachine.getExtendedState().getVariables().put("strategyParameter", strategyParameter);
        
        // Start the state machine
        stateMachine.start();
        
        // Send the initial event to begin the workflow
        System.out.println("\nStarting state machine workflow...");
        stateMachine.sendEvent(TerraformEvents.START_WORKFLOW);
        
        // Wait for the state machine to finish
        // In a real application, you might use listeners or reactive streams
        try {
            // Give the state machine time to complete all async operations
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Check final state
        TerraformStates finalState = stateMachine.getState().getId();
        
        System.out.println("\n═══════════════════════════════════════════════");
        if (finalState == TerraformStates.COMPLETED) {
            System.out.println("  ✓ State Machine completed successfully!");
            System.out.println("  Final State: " + finalState);
            System.out.println("  Final context attributes: " + context.getAttributeKeys());
        } else if (finalState == TerraformStates.ERROR) {
            System.out.println("  ✗ State Machine failed!");
            System.out.println("  Final State: " + finalState);
            System.exit(1);
        } else {
            System.out.println("  ⚠ State Machine did not complete");
            System.out.println("  Current State: " + finalState);
        }
        System.out.println("═══════════════════════════════════════════════");
        
        // Stop the state machine
        stateMachine.stop();
    }
}

