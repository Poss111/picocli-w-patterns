package com.example.workflow.commands;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "workflow3",
    description = "Run workflow 3",
    mixinStandardHelpOptions = true
)
public class Workflow3Command implements Runnable {

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
        System.out.println("=== Workflow 3 Execution ===");
        System.out.println("Workflow Name: " + workflowName);
        System.out.println("Scheduled Time: " + timeToRun);
        System.out.println("Status: Running Workflow 3...");
        
        // Simulate workflow execution
        try {
            Thread.sleep(1000);
            System.out.println("Status: Workflow 3 completed successfully!");
        } catch (InterruptedException e) {
            System.err.println("Workflow 3 was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}

