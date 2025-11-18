package com.example.workflow.commands;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.factory.WorkspaceNameFactory;
import com.example.workflow.factory.WorkspaceNameHandlerFactory;
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

    private final WorkspaceNameHandlerFactory workspaceNameHandlerFactory;
    private final TerraformInitHandler initHandler;
    private final TerraformValidateHandler validateHandler;
    private final TerraformPlanHandler planHandler;
    private final TerraformApplyHandler applyHandler;
    private final TerraformOutputHandler outputHandler;

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
     * Spring automatically injects all handler beans.
     * @Autowired is optional when there's only one constructor.
     */
    public Workflow1Command(WorkspaceNameHandlerFactory workspaceNameHandlerFactory,
                           TerraformInitHandler initHandler,
                           TerraformValidateHandler validateHandler,
                           TerraformPlanHandler planHandler,
                           TerraformApplyHandler applyHandler,
                           TerraformOutputHandler outputHandler) {
        this.workspaceNameHandlerFactory = workspaceNameHandlerFactory;
        this.initHandler = initHandler;
        this.validateHandler = validateHandler;
        this.planHandler = planHandler;
        this.applyHandler = applyHandler;
        this.outputHandler = outputHandler;
    }

    @Override
    public void run() {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Workflow 1: Terraform Deployment Pipeline");
        System.out.println("  Chain of Responsibility + Factory Pattern");
        System.out.println("  (With Context Passing & Input Validation)");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Workflow Name: " + workflowName);
        System.out.println("Scheduled Time: " + timeToRun);
        System.out.println("Naming Strategy: " + strategyType);
        System.out.println("═══════════════════════════════════════════════");
        
        // Create the context object that will be passed through the chain
        TerraformContext context = new TerraformContext(workflowName, timeToRun);
        
        // Optional: Add any initial context attributes
        context.setAttribute("environment", strategyParameter != null ? strategyParameter : "default");
        context.setAttribute("strategy_type", strategyType.toString());
        
        System.out.println("Created context with initial attributes: " + context.getAttributeKeys());
        
        // Use factory to create workspace name handler (cannot be Spring bean due to runtime parameters)
        TerraformHandler workspaceNameHandler = workspaceNameHandlerFactory.create(strategyType, strategyParameter);
        
        // Chain the Spring-managed handlers together
        // Workspace name generation comes first, followed by injected handlers
        workspaceNameHandler.setNext(initHandler)
                           .setNext(validateHandler)
                           .setNext(planHandler)
                           .setNext(applyHandler)
                           .setNext(outputHandler);
        
        // Start the chain with the context
        boolean success = workspaceNameHandler.handle(context);
        
        System.out.println("\n═══════════════════════════════════════════════");
        if (success) {
            System.out.println("  ✓ Pipeline completed successfully!");
            System.out.println("  Final context attributes: " + context.getAttributeKeys());
        } else {
            System.out.println("  ✗ Pipeline failed!");
            System.exit(1);
        }
        System.out.println("═══════════════════════════════════════════════");
    }
}

