package com.example.workflow.factory;

/**
 * Factory for creating different WorkspaceNameStrategy implementations.
 * Implements the Factory design pattern.
 */
public class WorkspaceNameFactory {
    
    /**
     * Enum defining available workspace naming strategy types.
     */
    public enum StrategyType {
        ENVIRONMENT,
        TIMESTAMP,
        CUSTOM_PREFIX,
        SIMPLE
    }
    
    /**
     * Creates a WorkspaceNameStrategy based on the specified type.
     * 
     * @param type the strategy type to create
     * @param parameter optional parameter for strategies that need it (e.g., environment name, prefix)
     * @return the created strategy
     * @throws IllegalArgumentException if parameter is required but not provided
     */
    public static WorkspaceNameStrategy createStrategy(StrategyType type, String parameter) {
        switch (type) {
            case ENVIRONMENT:
                if (parameter == null || parameter.trim().isEmpty()) {
                    throw new IllegalArgumentException("Environment name is required for ENVIRONMENT strategy");
                }
                return new EnvironmentBasedStrategy(parameter);
                
            case TIMESTAMP:
                return new TimestampBasedStrategy();
                
            case CUSTOM_PREFIX:
                if (parameter == null || parameter.trim().isEmpty()) {
                    throw new IllegalArgumentException("Prefix is required for CUSTOM_PREFIX strategy");
                }
                return new CustomPrefixStrategy(parameter);
                
            case SIMPLE:
                return new SimpleStrategy();
                
            default:
                throw new IllegalArgumentException("Unknown strategy type: " + type);
        }
    }
    
    /**
     * Creates a strategy with default parameter (useful for strategies that don't need parameters).
     * 
     * @param type the strategy type to create
     * @return the created strategy
     */
    public static WorkspaceNameStrategy createStrategy(StrategyType type) {
        return createStrategy(type, null);
    }
}

