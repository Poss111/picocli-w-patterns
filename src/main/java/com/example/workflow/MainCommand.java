package com.example.workflow;

import com.example.workflow.commands.*;
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
        Workflow3Command.class,
        Workflow5Command.class,
        Workflow6Command.class
    }
)
public class MainCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║         Workflow CLI - Design Patterns        ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println("\nAvailable workflows:");
        System.out.println("  workflow1  - Chain of Responsibility + Factory + Strategy");
        System.out.println("  workflow2  - Template Method + Factory + Strategy");
        System.out.println("  workflow3  - Simple workflow");
        System.out.println("  workflow5  - Personal Developer (fast & simple)");
        System.out.println("  workflow6  - Enterprise (secure & compliant)");
        System.out.println("\nUse --help with any workflow to see options.");
        System.out.println("\nExamples:");
        System.out.println("  workflow-cli workflow5 -n 'Quick Test' -t 'now'");
        System.out.println("  workflow-cli workflow6 -n 'Production Deploy' --environment production");
    }
}

