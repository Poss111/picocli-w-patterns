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

    @Override
    public void run() {
        // Create an instance of the template implementation
        Workflow2TerraformTemplate template = new Workflow2TerraformTemplate();
        
        // Execute the workflow using the template method
        boolean success = template.executeWorkflow(workflowName, timeToRun);
        
        if (!success) {
            System.exit(1);
        }
    }
}

