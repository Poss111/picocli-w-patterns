package com.example.workflow.template;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.factory.WorkspaceNameFactory;
import com.example.workflow.factory.WorkspaceNameStrategy;
import com.example.workflow.template.exceptions.*;

/**
 * Abstract template class for Terraform workflow execution.
 * Implements the Template Method design pattern with comprehensive error handling.
 * 
 * The template method defines the skeleton of the algorithm (Terraform workflow),
 * with concrete subclasses implementing the specific steps.
 * 
 * Error handling features:
 * - Custom exceptions for each stage
 * - Error hooks for custom error handling
 * - Retry mechanism for recoverable errors
 * - Cleanup/rollback support
 * - Context passing for shared state between stages
 */
public abstract class TerraformWorkflowTemplate {
    
    protected String workflowName;
    protected String timeToRun;
    protected int maxRetries = 2;
    protected TerraformContext context;
    
    /**
     * Template method that defines the workflow skeleton with error handling.
     * This method cannot be overridden to ensure the workflow structure is maintained.
     * 
     * @param workflowName the name of the workflow
     * @param timeToRun the scheduled time to run
     * @param context the shared context object for passing state between stages
     * @return true if workflow completed successfully, false otherwise
     */
    public final boolean executeWorkflow(String workflowName, String timeToRun, TerraformContext context) {
        this.workflowName = workflowName;
        this.timeToRun = timeToRun;
        this.context = context;
        
        printHeader();
        
        System.out.println("Initialized context with attributes: " + context.getAttributeKeys());
        System.out.println("═══════════════════════════════════════════════");
        
        try {
            // Execute each stage in order with error handling
            executeWithRetry(() -> {
                try {
                    return init(context);
                } catch (Exception e) {
                    throw new InitializationException("Initialization failed: " + e.getMessage(), e);
                }
            }, "init");
            
            executeWithRetry(() -> {
                try {
                    return validate(context);
                } catch (Exception e) {
                    throw new ValidationException("Validation failed: " + e.getMessage(), e);
                }
            }, "validate");
            
            executeWithRetry(() -> {
                try {
                    return plan(context);
                } catch (Exception e) {
                    throw new PlanningException("Planning failed: " + e.getMessage(), e);
                }
            }, "plan");
            
            executeWithRetry(() -> {
                try {
                    return apply(context);
                } catch (Exception e) {
                    throw new ApplyException("Apply failed: " + e.getMessage(), e);
                }
            }, "apply");
            
            executeWithRetry(() -> {
                try {
                    return output(context);
                } catch (Exception e) {
                    throw new OutputException("Output retrieval failed: " + e.getMessage(), e);
                }
            }, "output");
            
            printFooter(true);
            return true;
            
        } catch (TerraformWorkflowException e) {
            handleError(e);
            printFooter(false);
            return false;
        }
    }
    
    /**
     * Legacy method for backward compatibility.
     * Creates a context and delegates to the context-based method.
     * 
     * @param workflowName the name of the workflow
     * @param timeToRun the scheduled time to run
     * @return true if workflow completed successfully, false otherwise
     * @deprecated Use {@link #executeWorkflow(String, String, TerraformContext)} instead
     */
    @Deprecated
    public final boolean executeWorkflow(String workflowName, String timeToRun) {
        TerraformContext context = new TerraformContext(workflowName, timeToRun);
        return executeWorkflow(workflowName, timeToRun, context);
    }
    
    /**
     * Executes a stage with retry logic for recoverable errors.
     * 
     * @param stage the stage to execute
     * @param stageName the name of the stage
     * @throws TerraformWorkflowException if the stage fails after retries
     */
    private void executeWithRetry(StageExecutor stage, String stageName) throws TerraformWorkflowException {
        int attempts = 0;
        TerraformWorkflowException lastException = null;
        
        while (attempts <= maxRetries) {
            try {
                boolean result = stage.execute();
                if (result) {
                    return; // Success
                } else {
                    throw new TerraformWorkflowException(stageName, 
                        "Stage returned false", true);
                }
            } catch (TerraformWorkflowException e) {
                lastException = e;
                attempts++;
                
                if (e.isRecoverable() && attempts <= maxRetries) {
                    System.err.println("⚠ Stage failed (attempt " + attempts + "/" + (maxRetries + 1) + "): " + e.getMessage());
                    System.err.println("  Retrying in " + (attempts * 2) + " seconds...");
                    
                    // Call error hook
                    onStageError(e, attempts);
                    
                    try {
                        Thread.sleep(attempts * 2000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new TerraformWorkflowException(stageName, 
                            "Interrupted during retry wait", ie, false);
                    }
                } else {
                    // Not recoverable or max retries reached
                    throw e;
                }
            }
        }
        
        // If we get here, we've exhausted retries
        if (lastException != null) {
            throw lastException;
        }
    }
    
    /**
     * Functional interface for stage execution.
     */
    @FunctionalInterface
    private interface StageExecutor {
        boolean execute() throws TerraformWorkflowException;
    }
    
    /**
     * Hook method - can be overridden to customize header display.
     */
    protected void printHeader() {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Terraform Workflow Template");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Workflow Name: " + workflowName);
        System.out.println("Scheduled Time: " + timeToRun);
        System.out.println("═══════════════════════════════════════════════");
    }
    
    /**
     * Hook method - can be overridden to customize footer display.
     */
    protected void printFooter(boolean success) {
        System.out.println("\n═══════════════════════════════════════════════");
        if (success) {
            System.out.println("  ✓ Workflow completed successfully!");
        } else {
            System.out.println("  ✗ Workflow failed!");
        }
        System.out.println("═══════════════════════════════════════════════");
    }
    
    /**
     * Hook method - called when a stage encounters an error before retry.
     * Can be overridden to implement custom error recovery logic.
     * 
     * @param exception the exception that occurred
     * @param attemptNumber the current attempt number
     */
    protected void onStageError(TerraformWorkflowException exception, int attemptNumber) {
        // Default implementation does nothing
        // Subclasses can override to add custom error recovery
    }
    
    /**
     * Hook method - called when workflow fails after all retries.
     * Can be overridden to implement cleanup or rollback logic.
     * 
     * @param exception the final exception that caused failure
     */
    protected void handleError(TerraformWorkflowException exception) {
        System.err.println("\n✗ Workflow failed at stage: " + exception.getStage());
        System.err.println("  Error: " + exception.getMessage());
        System.err.println("  Recoverable: " + exception.isRecoverable());
        
        if (exception.getCause() != null) {
            System.err.println("  Caused by: " + exception.getCause().getMessage());
        }
        
        // Call cleanup hook
        cleanup(exception);
    }
    
    /**
     * Hook method - called to perform cleanup after a workflow failure.
     * Can be overridden to implement rollback logic.
     * 
     * @param exception the exception that caused the failure
     */
    protected void cleanup(TerraformWorkflowException exception) {
        // Default implementation does nothing
        // Subclasses can override to add cleanup/rollback logic
        System.err.println("  Performing cleanup...");
    }
    
    /**
     * Sets the maximum number of retries for recoverable errors.
     * 
     * @param maxRetries the maximum number of retries
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    // Abstract methods - must be implemented by concrete classes
    
    /**
     * Terraform Init stage - provides a shared implementation that can be used by all workflows.
     * This method demonstrates code reuse in the Template Method pattern.
     * Subclasses can override this if they need custom initialization behavior.
     * 
     * Default implementation:
     * - Generates workspace name using Factory + Strategy patterns
     * - Initializes Terraform with standard providers
     * - Configures backend
     * - Stores initialization metadata in context
     * 
     * @param context the shared context object
     * @return true if init succeeded, false otherwise
     */
    protected boolean init(TerraformContext context) {
        System.out.println("\n[Stage 1/5] Terraform Init (Shared Implementation)");
        System.out.println("─────────────────────────────");
        
        // Generate workspace name using Factory + Strategy patterns
        System.out.println("▸ Generating workspace name using Factory pattern...");
        
        // Retrieve strategy type and parameter from context
        String strategyTypeStr = context.getAttribute("strategy_type", String.class);
        String strategyParameter = context.getAttribute("strategy_parameter", String.class);
        
        WorkspaceNameFactory.StrategyType strategyType = strategyTypeStr != null 
            ? WorkspaceNameFactory.StrategyType.valueOf(strategyTypeStr)
            : WorkspaceNameFactory.StrategyType.TIMESTAMP;
        
        // Use Factory to create the appropriate strategy
        WorkspaceNameStrategy strategy;
        if (strategyParameter != null && !strategyParameter.equals("N/A")) {
            strategy = WorkspaceNameFactory.createStrategy(strategyType, strategyParameter);
        } else {
            strategy = WorkspaceNameFactory.createStrategy(strategyType);
        }
        
        // Generate workspace name using the selected strategy
        String workspaceName = strategy.generateWorkspaceName(context.getWorkflowName(), context.getTimeToRun());
        
        System.out.println("  Strategy: " + strategy.getStrategyDescription());
        System.out.println("  Workspace: " + workspaceName);
        
        // Store workspace name in context
        context.setAttribute("workspace_name", workspaceName);
        System.out.println("  [Context] Stored workspace name");
        
        System.out.println("\n▸ Initializing Terraform...");
        System.out.println("  Downloading providers...");
        
        try {
            Thread.sleep(500);
            System.out.println("  • provider registry.terraform.io/hashicorp/aws v5.0.0");
            System.out.println("  • provider registry.terraform.io/hashicorp/random v3.5.0");
            
            // Store initialization info in context
            context.setAttribute("providers_initialized", true);
            context.setAttribute("aws_provider_version", "5.0.0");
            context.setAttribute("backend_configured", true);
            
            System.out.println("✓ Terraform initialized successfully!");
            System.out.println("  [Context] Stored initialization metadata");
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Init failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Terraform Validate stage - must be implemented by subclasses.
     * 
     * @param context the shared context object
     * @return true if validation succeeded, false otherwise
     */
    protected abstract boolean validate(TerraformContext context);
    
    /**
     * Terraform Plan stage - must be implemented by subclasses.
     * 
     * @param context the shared context object
     * @return true if planning succeeded, false otherwise
     */
    protected abstract boolean plan(TerraformContext context);
    
    /**
     * Terraform Apply stage - must be implemented by subclasses.
     * 
     * @param context the shared context object
     * @return true if apply succeeded, false otherwise
     */
    protected abstract boolean apply(TerraformContext context);
    
    /**
     * Terraform Output stage - must be implemented by subclasses.
     * 
     * @param context the shared context object
     * @return true if output retrieval succeeded, false otherwise
     */
    protected abstract boolean output(TerraformContext context);
}

