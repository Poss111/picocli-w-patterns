package com.example.workflow.statemachine.actions;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.statemachine.TerraformEvents;
import com.example.workflow.statemachine.TerraformStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * Action executed when entering the TERRAFORM_INIT state.
 */
@Component
public class TerraformInitAction implements Action<TerraformStates, TerraformEvents> {
    
    @Override
    public void execute(StateContext<TerraformStates, TerraformEvents> stateContext) {
        System.out.println("\n[State Machine - Stage 1/5] Terraform Init");
        System.out.println("─────────────────────────────");
        
        TerraformContext context = stateContext.getExtendedState()
                .get("context", TerraformContext.class);
        
        String workspaceName = context.getAttribute("workspace_name", String.class);
        System.out.println("Workspace: " + workspaceName);
        System.out.println("Initializing Terraform working directory...");
        System.out.println("Downloading provider plugins...");
        
        try {
            Thread.sleep(500);
            
            context.setAttribute("terraform_initialized", true);
            context.setAttribute("provider_version", "aws v5.0.0");
            
            System.out.println("✓ Terraform has been successfully initialized!");
            
            stateContext.getStateMachine().sendEvent(TerraformEvents.INIT_COMPLETED);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Init stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            stateContext.getStateMachine().sendEvent(TerraformEvents.ERROR_OCCURRED);
        }
    }
}

