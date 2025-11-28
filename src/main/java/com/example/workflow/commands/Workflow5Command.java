package com.example.workflow.commands;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.template.PersonalDevTerraformTemplate;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "workflow5",
    description = "Personal Developer Workflow - Fast local development with minimal overhead",
    mixinStandardHelpOptions = true
)
public class Workflow5Command implements Runnable {

    private final PersonalDevTerraformTemplate template;

    @Option(
        names = {"-n", "--name"},
        description = "Name of the workflow",
        required = true
    )
    private String workflowName;

    @Option(
        names = {"-t", "--time"},
        description = "Time to run the workflow",
        defaultValue = "now"
    )
    private String timeToRun;
    
    @Option(
        names = {"-r", "--retries"},
        description = "Maximum number of retries (default: 1 for fast feedback)",
        defaultValue = "1"
    )
    private int maxRetries;
    
    /**
     * Constructor with dependency injection.
     */
    public Workflow5Command(PersonalDevTerraformTemplate template) {
        this.template = template;
    }

    @Override
    public void run() {
        System.out.println("\n🚀 Starting Personal Developer Workflow...\n");
        
        // Create context
        TerraformContext context = new TerraformContext(workflowName, timeToRun);
        context.setAttribute("developer_mode", true);
        context.setAttribute("environment", "local");
        context.setAttribute("fast_mode", true);
        
        // Configure for quick iteration
        template.setMaxRetries(maxRetries);
        
        // Execute
        boolean success = template.executeWorkflow(workflowName, timeToRun, context);
        
        if (!success) {
            System.exit(1);
        }
    }
}

