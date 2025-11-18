package com.example.workflow.handlers;

import com.example.workflow.context.TerraformContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class for Terraform stage handlers in the Chain of Responsibility pattern.
 * Now includes context passing and input validation capabilities.
 */
public abstract class TerraformHandler {
    
    protected TerraformHandler nextHandler;
    
    /**
     * Sets the next handler in the chain.
     * 
     * @param nextHandler the next handler to be called
     * @return the next handler (for fluent chaining)
     */
    public TerraformHandler setNext(TerraformHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }
    
    /**
     * Handles the Terraform stage execution with context.
     * This method performs validation before delegating to the implementation.
     * 
     * @param context the shared context object
     * @return true if the stage completed successfully, false otherwise
     */
    public final boolean handle(TerraformContext context) {
        // Validate required attributes before execution
        Set<String> requiredAttributes = getRequiredAttributes();
        if (!validateContext(context, requiredAttributes)) {
            return false;
        }
        
        // Execute the handler's logic
        return doHandle(context);
    }
    
    /**
     * Performs the actual handler logic. Subclasses must implement this.
     * 
     * @param context the shared context object
     * @return true if the stage completed successfully, false otherwise
     */
    protected abstract boolean doHandle(TerraformContext context);
    
    /**
     * Defines the required attributes that must exist in the context.
     * Subclasses can override this to specify their requirements.
     * 
     * @return a set of required attribute keys (empty by default)
     */
    protected Set<String> getRequiredAttributes() {
        return new HashSet<>();
    }
    
    /**
     * Validates that all required attributes exist in the context.
     * 
     * @param context the context to validate
     * @param requiredAttributes the set of required attribute keys
     * @return true if validation passes, false otherwise
     */
    private boolean validateContext(TerraformContext context, Set<String> requiredAttributes) {
        if (requiredAttributes == null || requiredAttributes.isEmpty()) {
            return true; // No validation required
        }
        
        for (String attribute : requiredAttributes) {
            if (!context.hasAttribute(attribute)) {
                System.err.println("✗ Validation failed: Required attribute '" + attribute + "' not found in context");
                System.err.println("  Handler: " + this.getClass().getSimpleName());
                System.err.println("  Available attributes: " + context.getAttributeKeys());
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Passes the request to the next handler in the chain.
     * 
     * @param context the shared context object
     * @return true if all remaining stages complete successfully, false otherwise
     */
    protected boolean passToNext(TerraformContext context) {
        if (nextHandler != null) {
            return nextHandler.handle(context);
        }
        return true;
    }
    
    /**
     * Helper method to create a set of required attributes.
     * 
     * @param attributes the required attribute keys
     * @return a set containing the specified attributes
     */
    protected Set<String> requireAttributes(String... attributes) {
        return new HashSet<>(Arrays.asList(attributes));
    }
    
    // Legacy method for backward compatibility - delegates to context-based method
    @Deprecated
    public boolean handle(String workflowName, String timeToRun) {
        TerraformContext context = new TerraformContext(workflowName, timeToRun);
        return handle(context);
    }
}

