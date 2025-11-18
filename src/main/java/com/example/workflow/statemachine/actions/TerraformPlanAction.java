package com.example.workflow.statemachine.actions;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.statemachine.TerraformEvents;
import com.example.workflow.statemachine.TerraformStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * Action executed when entering the TERRAFORM_PLAN state.
 */
@Component
public class TerraformPlanAction implements Action<TerraformStates, TerraformEvents> {
    
    @Override
    public void execute(StateContext<TerraformStates, TerraformEvents> stateContext) {
        System.out.println("\n[State Machine - Stage 3/5] Terraform Plan");
        System.out.println("─────────────────────────────");
        
        TerraformContext context = stateContext.getExtendedState()
                .get("context", TerraformContext.class);
        
        String workspaceName = context.getAttribute("workspace_name", String.class);
        System.out.println("Creating execution plan for: " + context.getWorkflowName());
        System.out.println("Workspace: " + workspaceName);
        System.out.println("Scheduled for: " + context.getTimeToRun());
        System.out.println("Analyzing resource changes...");
        
        try {
            Thread.sleep(600);
            
            context.setAttribute("plan_created", true);
            context.setAttribute("resources_to_add", 3);
            context.setAttribute("resources_to_change", 1);
            context.setAttribute("resources_to_destroy", 0);
            
            System.out.println("Plan: 3 to add, 1 to change, 0 to destroy.");
            System.out.println("✓ Plan created successfully!");
            
            stateContext.getStateMachine().sendEvent(TerraformEvents.PLAN_COMPLETED);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Plan stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            stateContext.getStateMachine().sendEvent(TerraformEvents.ERROR_OCCURRED);
        }
    }
}

