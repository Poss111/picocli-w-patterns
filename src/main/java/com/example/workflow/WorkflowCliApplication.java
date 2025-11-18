package com.example.workflow;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
public class WorkflowCliApplication implements CommandLineRunner, ExitCodeGenerator {

    private final IFactory factory;
    private final MainCommand mainCommand;
    private int exitCode;

    public WorkflowCliApplication(IFactory factory, MainCommand mainCommand) {
        this.factory = factory;
        this.mainCommand = mainCommand;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(WorkflowCliApplication.class, args)));
    }

    @Override
    public void run(String... args) {
        exitCode = new CommandLine(mainCommand, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}

