package com.example.workflow.statemachine.actions;

import com.example.workflow.context.TerraformContext;
import com.example.workflow.statemachine.TerraformEvents;
import com.example.workflow.statemachine.TerraformStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * Action executed when entering the TERRAFORM_OUTPUT state.
 */
@Component
public class TerraformOutputAction implements Action<TerraformStates, TerraformEvents> {
    
    @Override
    public void execute(StateContext<TerraformStates, TerraformEvents> stateContext) {
        System.out.println("\n[State Machine - Stage 5/5] Terraform Output");
        System.out.println("─────────────────────────────");
        
        TerraformContext context = stateContext.getExtendedState()
                .get("context", TerraformContext.class);
        
        System.out.println("Retrieving output values...");
        
        try {
            Thread.sleep(300);
            
            String instanceId = context.getAttribute("instance_id", String.class);
            String securityGroupId = context.getAttribute("security_group_id", String.class);
            String workspaceName = context.getAttribute("workspace_name", String.class);
            
            System.out.println("\nOutputs:");
            System.out.println("  workspace_name = \"" + workspaceName + "\"");
            System.out.println("  instance_id = \"" + (instanceId != null ? instanceId : "unknown") + "\"");
            System.out.println("  public_ip = \"54.123.45.67\"");
            System.out.println("  security_group_id = \"" + (securityGroupId != null ? securityGroupId : "unknown") + "\"");
            System.out.println("\n✓ Workflow '" + context.getWorkflowName() + "' completed successfully!");
            System.out.println("  All Terraform stages executed at: " + context.getTimeToRun());
            System.out.println("  Total context attributes: " + context.getAttributeKeys().size());
            
            stateContext.getStateMachine().sendEvent(TerraformEvents.OUTPUT_COMPLETED);
            
        } catch (InterruptedException e) {
            System.err.println("✗ Output stage failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            stateContext.getStateMachine().sendEvent(TerraformEvents.ERROR_OCCURRED);
        }
    }
}

