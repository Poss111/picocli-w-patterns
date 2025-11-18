package com.example.workflow.statemachine.actions;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.factory.WorkspaceNameFactory;
import com.example.workflow.factory.WorkspaceNameStrategy;
import com.example.workflow.statemachine.TerraformEvents;
import com.example.workflow.statemachine.TerraformStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * Action executed when entering the WORKSPACE_NAME_GENERATION state.
 */
@Component
public class WorkspaceNameGenerationAction implements Action<TerraformStates, TerraformEvents> {
    
    @Override
    public void execute(StateContext<TerraformStates, TerraformEvents> stateContext) {
        System.out.println("\n[State Machine - Stage 0/5] Workspace Name Generation");
        System.out.println("─────────────────────────────");
        
        // Get context from state machine extended state
        TerraformContext context = stateContext.getExtendedState()
                .get("context", TerraformContext.class);
        
        WorkspaceNameFactory.StrategyType strategyType = stateContext.getExtendedState()
                .get("strategyType", WorkspaceNameFactory.StrategyType.class);
        
        String strategyParameter = stateContext.getExtendedState()
                .get("strategyParameter", String.class);
        
        try {
            System.out.println("Using Factory pattern to create workspace naming strategy...");
            WorkspaceNameStrategy strategy = WorkspaceNameFactory.createStrategy(strategyType, strategyParameter);
            
            System.out.println("Strategy: " + strategy.getStrategyDescription());
            
            String workspaceName = strategy.generateWorkspaceName(
                    context.getWorkflowName(), 
                    context.getTimeToRun());
            
            context.setAttribute("workspace_name", workspaceName);
            
            System.out.println("Generated workspace name: " + workspaceName);
            System.out.println("✓ Workspace name determined successfully!");
            
            // Send success event to transition to next state
            stateContext.getStateMachine().sendEvent(TerraformEvents.WORKSPACE_NAME_GENERATED);
            
        } catch (Exception e) {
            System.err.println("✗ Workspace name generation failed: " + e.getMessage());
            stateContext.getStateMachine().sendEvent(TerraformEvents.ERROR_OCCURRED);
        }
    }
}

