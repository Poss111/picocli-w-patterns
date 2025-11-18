package com.example.workflow.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Context object passed through the Chain of Responsibility.
 * Allows handlers to share state and attributes during workflow execution.
 */
public class TerraformContext {
    
    private final String workflowName;
    private final String timeToRun;
    private final Map<String, Object> attributes;
    
    /**
     * Creates a new TerraformContext with workflow metadata.
     * 
     * @param workflowName the name of the workflow
     * @param timeToRun the scheduled time to run
     */
    public TerraformContext(String workflowName, String timeToRun) {
        this.workflowName = workflowName;
        this.timeToRun = timeToRun;
        this.attributes = new HashMap<>();
    }
    
    /**
     * Gets the workflow name.
     * 
     * @return the workflow name
     */
    public String getWorkflowName() {
        return workflowName;
    }
    
    /**
     * Gets the scheduled time to run.
     * 
     * @return the time to run
     */
    public String getTimeToRun() {
        return timeToRun;
    }
    
    /**
     * Sets an attribute in the context.
     * 
     * @param key the attribute key
     * @param value the attribute value
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    /**
     * Gets an attribute from the context.
     * 
     * @param key the attribute key
     * @return the attribute value, or null if not found
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * Gets a typed attribute from the context.
     * 
     * @param <T> the type of the attribute
     * @param key the attribute key
     * @param type the class of the expected type
     * @return the typed attribute value, or null if not found or wrong type
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, Class<T> type) {
        Object value = attributes.get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * Checks if an attribute exists in the context.
     * 
     * @param key the attribute key
     * @return true if the attribute exists, false otherwise
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    /**
     * Removes an attribute from the context.
     * 
     * @param key the attribute key
     * @return the removed value, or null if not found
     */
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }
    
    /**
     * Gets all attribute keys.
     * 
     * @return a set of all attribute keys
     */
    public java.util.Set<String> getAttributeKeys() {
        return attributes.keySet();
    }
    
    /**
     * Clears all attributes from the context.
     */
    public void clearAttributes() {
        attributes.clear();
    }
    
    @Override
    public String toString() {
        return "TerraformContext{" +
                "workflowName='" + workflowName + '\'' +
                ", timeToRun='" + timeToRun + '\'' +
                ", attributes=" + attributes.keySet() +
                '}';
    }
}

