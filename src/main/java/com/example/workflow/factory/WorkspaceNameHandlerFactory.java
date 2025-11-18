package com.example.workflow.factory;

import com.example.workflow.handlers.WorkspaceNameHandler;
import org.springframework.stereotype.Component;

/**
 * Spring-managed factory for creating WorkspaceNameHandler instances.
 * Since handlers need to be created with specific parameters at runtime,
 * we use a factory bean to create them.
 */
@Component
public class WorkspaceNameHandlerFactory {
    
    /**
     * Creates a new WorkspaceNameHandler with the specified strategy configuration.
     * 
     * @param strategyType the type of naming strategy to use
     * @param strategyParameter optional parameter for the strategy
     * @return a new WorkspaceNameHandler instance
     */
    public WorkspaceNameHandler create(WorkspaceNameFactory.StrategyType strategyType, 
                                       String strategyParameter) {
        return new WorkspaceNameHandler(strategyType, strategyParameter);
    }
    
    /**
     * Creates a new WorkspaceNameHandler with the specified strategy type (no parameter).
     * 
     * @param strategyType the type of naming strategy to use
     * @return a new WorkspaceNameHandler instance
     */
    public WorkspaceNameHandler create(WorkspaceNameFactory.StrategyType strategyType) {
        return new WorkspaceNameHandler(strategyType);
    }
}

