package com.example.workflow.commands;

import com.example.workflow.template.Workflow2TerraformTemplate;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "workflow2",
    description = "Run workflow 2 - Terraform deployment using Template Method pattern",
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
        // Execute the workflow using the injected template
        boolean success = template.executeWorkflow(workflowName, timeToRun);
        
        if (!success) {
            System.exit(1);
        }
    }
}

