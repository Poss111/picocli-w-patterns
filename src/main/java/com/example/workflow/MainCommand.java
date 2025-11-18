package com.example.workflow;

import com.example.workflow.commands.Workflow1Command;
import com.example.workflow.commands.Workflow2Command;
import com.example.workflow.commands.Workflow3Command;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(
    name = "workflow-cli",
    mixinStandardHelpOptions = true,
    version = "1.0.0",
    description = "CLI tool for running workflows",
    subcommands = {
        Workflow1Command.class,
        Workflow2Command.class,
        Workflow3Command.class
    }
)
public class MainCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Please specify a command. Use --help to see available commands.");
    }
}

