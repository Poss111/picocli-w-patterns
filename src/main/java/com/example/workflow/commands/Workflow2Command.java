package com.example.workflow.commands;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.factory.WorkspaceNameFactory;
import com.example.workflow.template.Workflow2TerraformTemplate;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "workflow2",
    description = "Run workflow 2 - Terraform deployment using Template Method pattern with error handling",
    mixinStandardHelpOptions = true
)
public class Workflow2Command implements Runnable {

    private final Workflow2TerraformTemplate template;

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
        names = {"-r", "--retries"},
        description = "Maximum number of retries for recoverable errors (default: 2)",
        defaultValue = "2"
    )
    private int maxRetries;
    
    @Option(
        names = {"--simulate-failure"},
        description = "Simulate failure at specific stage for testing: INIT, VALIDATE, PLAN, APPLY, OUTPUT"
    )
    private String simulateFailureStage;
    
    @Option(
        names = {"--failure-attempts"},
        description = "Number of attempts before success when simulating failure (default: fail all attempts)",
        defaultValue = "999"
    )
    private int failureAttempts;
    
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
     * Spring automatically injects the template bean.
     * @Autowired is optional when there's only one constructor.
     */
    public Workflow2Command(Workflow2TerraformTemplate template) {
        this.template = template;
    }

    @Override
    public void run() {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Starting Workflow 2");
        System.out.println("  Template Method Pattern with Context Passing");
        System.out.println("═══════════════════════════════════════════════");
        
        try {
            // Create the context object that will be passed through the template stages
            TerraformContext context = new TerraformContext(workflowName, timeToRun);
            
            // Add initial context attributes
            context.setAttribute("environment", "production");
            context.setAttribute("region", "us-east-1");
            context.setAttribute("pattern", "Template Method");
            context.setAttribute("strategy_type", strategyType.toString());
            context.setAttribute("strategy_parameter", strategyParameter != null ? strategyParameter : "N/A");
            
            System.out.println("Created context with initial attributes: " + context.getAttributeKeys());
            System.out.println("Workspace Strategy: " + strategyType);
            if (strategyParameter != null) {
                System.out.println("Strategy Parameter: " + strategyParameter);
            }
            System.out.println("═══════════════════════════════════════════════\n");
            
            // Configure retry settings
            template.setMaxRetries(maxRetries);
            
            // Configure failure simulation for testing
            if (simulateFailureStage != null && !simulateFailureStage.isEmpty()) {
                System.out.println("⚠ TEST MODE: Simulating failure at stage: " + simulateFailureStage);
                System.out.println("⚠ Failure will occur for " + 
                    (failureAttempts == 999 ? "all attempts" : failureAttempts + " attempt(s)"));
                System.out.println("═══════════════════════════════════════════════\n");
                template.enableFailureSimulation(simulateFailureStage, failureAttempts);
            }
            
            // Execute the workflow using the injected template with context
            boolean success = template.executeWorkflow(workflowName, timeToRun, context);
            
            if (success) {
                System.out.println("\n═══════════════════════════════════════════════");
                System.out.println("  ✓ Workflow 2 completed successfully!");
                System.out.println("  Final context attributes: " + context.getAttributeKeys());
                System.out.println("═══════════════════════════════════════════════");
            } else {
                System.err.println("\n═══════════════════════════════════════════════");
                System.err.println("  ✗ Workflow 2 failed!");
                System.err.println("  Check error messages above for details.");
                System.err.println("═══════════════════════════════════════════════");
                System.exit(1);
            }
            
        } catch (Exception e) {
            // Catch any unexpected exceptions
            System.err.println("\n═══════════════════════════════════════════════");
            System.err.println("  ✗ Unexpected error in Workflow 2!");
            System.err.println("  Error: " + e.getMessage());
            System.err.println("═══════════════════════════════════════════════");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

