package com.example.workflow.statemachine.actions;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.statemachine.TerraformEvents;
import com.example.workflow.statemachine.TerraformStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * Action executed when entering the TERRAFORM_APPLY state.
 */
@Component
public class TerraformApplyAction implements Action<TerraformStates, TerraformEvents> {
    
    @Override
    public void execute(StateContext<TerraformStates, TerraformEvents> stateContext) {
        System.out.println("\n[State Machine - Stage 4/5] Terraform Apply");
        System.out.println("─────────────────────────────");
        
        TerraformContext context = stateContext.getExtendedState()
                .get("context", TerraformContext.class);
        
        System.out.println("Applying Terraform changes...");
        System.out.println("Creating resources...");
        
        try {
            Thread.sleep(800);
            System.out.println("aws_instance.web_server: Creating...");
            Thread.sleep(300);
            System.out.println("aws_instance.web_server: Creation complete");
            System.out.println("aws_security_group.allow_http: Creating...");
            Thread.sleep(300);
            System.out.println("aws_security_group.allow_http: Creation complete");
            
            context.setAttribute("apply_complete", true);
            context.setAttribute("instance_id", "i-1234567890abcdef0");
            context.setAttribute("security_group_id", "sg-0123456789abcdef0");
            
            Integer added = context.getAttribute("resources_to_add", Integer.class);
            Integer changed = context.getAttribute("resources_to_change", Integer.class);
            Integer destroyed = context.getAttribute("resources_to_destroy", Integer.class);
            
            System.out.println("✓ Apply complete! Resources: " + 
                             (added != null ? added : 0) + " added, " +
                             (changed != null ? changed : 0) + " changed, " +
                             (destroyed != null ? destroyed : 0) + " destroyed.");
            
            stateContext.getStateMachine().sendEvent(TerraformEvents.APPLY_COMPLETED);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Apply stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            stateContext.getStateMachine().sendEvent(TerraformEvents.ERROR_OCCURRED);
        }
    }
}

