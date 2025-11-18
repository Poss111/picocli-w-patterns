package com.example.workflow.statemachine.actions;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.statemachine.TerraformEvents;
import com.example.workflow.statemachine.TerraformStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * Action executed when entering the TERRAFORM_VALIDATE state.
 */
@Component
public class TerraformValidateAction implements Action<TerraformStates, TerraformEvents> {
    
    @Override
    public void execute(StateContext<TerraformStates, TerraformEvents> stateContext) {
        System.out.println("\n[State Machine - Stage 2/5] Terraform Validate");
        System.out.println("─────────────────────────────");
        
        TerraformContext context = stateContext.getExtendedState()
                .get("context", TerraformContext.class);
        
        System.out.println("Validating Terraform configuration files...");
        System.out.println("Checking syntax and consistency...");
        
        try {
            Thread.sleep(400);
            
            context.setAttribute("configuration_valid", true);
            
            System.out.println("✓ Configuration is valid!");
            
            stateContext.getStateMachine().sendEvent(TerraformEvents.VALIDATE_COMPLETED);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Validation stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            stateContext.getStateMachine().sendEvent(TerraformEvents.ERROR_OCCURRED);
        }
    }
}

