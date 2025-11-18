package com.example.workflow.statemachine;

import com.example.workflow.statemachine.actions.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

/**
 * Configuration for the Terraform workflow state machine.
 * Uses the State pattern implemented through Spring State Machine.
 */
@Configuration
@EnableStateMachineFactory
public class TerraformStateMachineConfig 
        extends EnumStateMachineConfigurerAdapter<TerraformStates, TerraformEvents> {
    
    private final WorkspaceNameGenerationAction workspaceNameGenerationAction;
    private final TerraformInitAction terraformInitAction;
    private final TerraformValidateAction terraformValidateAction;
    private final TerraformPlanAction terraformPlanAction;
    private final TerraformApplyAction terraformApplyAction;
    private final TerraformOutputAction terraformOutputAction;
    
    public TerraformStateMachineConfig(
            WorkspaceNameGenerationAction workspaceNameGenerationAction,
            TerraformInitAction terraformInitAction,
            TerraformValidateAction terraformValidateAction,
            TerraformPlanAction terraformPlanAction,
            TerraformApplyAction terraformApplyAction,
            TerraformOutputAction terraformOutputAction) {
        this.workspaceNameGenerationAction = workspaceNameGenerationAction;
        this.terraformInitAction = terraformInitAction;
        this.terraformValidateAction = terraformValidateAction;
        this.terraformPlanAction = terraformPlanAction;
        this.terraformApplyAction = terraformApplyAction;
        this.terraformOutputAction = terraformOutputAction;
    }
    
    @Override
    public void configure(StateMachineConfigurationConfigurer<TerraformStates, TerraformEvents> config)
            throws Exception {
        config
            .withConfiguration()
            .autoStartup(false); // We'll start it manually
    }
    
    @Override
    public void configure(StateMachineStateConfigurer<TerraformStates, TerraformEvents> states)
            throws Exception {
        states
            .withStates()
            .initial(TerraformStates.INITIAL)
            .state(TerraformStates.WORKSPACE_NAME_GENERATION, workspaceNameGenerationAction)
            .state(TerraformStates.TERRAFORM_INIT, terraformInitAction)
            .state(TerraformStates.TERRAFORM_VALIDATE, terraformValidateAction)
            .state(TerraformStates.TERRAFORM_PLAN, terraformPlanAction)
            .state(TerraformStates.TERRAFORM_APPLY, terraformApplyAction)
            .state(TerraformStates.TERRAFORM_OUTPUT, terraformOutputAction)
            .end(TerraformStates.COMPLETED)
            .end(TerraformStates.ERROR);
    }
    
    @Override
    public void configure(StateMachineTransitionConfigurer<TerraformStates, TerraformEvents> transitions)
            throws Exception {
        transitions
            // Start workflow
            .withExternal()
                .source(TerraformStates.INITIAL)
                .target(TerraformStates.WORKSPACE_NAME_GENERATION)
                .event(TerraformEvents.START_WORKFLOW)
                .and()
            
            // Workspace name generated -> Init
            .withExternal()
                .source(TerraformStates.WORKSPACE_NAME_GENERATION)
                .target(TerraformStates.TERRAFORM_INIT)
                .event(TerraformEvents.WORKSPACE_NAME_GENERATED)
                .and()
            
            // Init completed -> Validate
            .withExternal()
                .source(TerraformStates.TERRAFORM_INIT)
                .target(TerraformStates.TERRAFORM_VALIDATE)
                .event(TerraformEvents.INIT_COMPLETED)
                .and()
            
            // Validate completed -> Plan
            .withExternal()
                .source(TerraformStates.TERRAFORM_VALIDATE)
                .target(TerraformStates.TERRAFORM_PLAN)
                .event(TerraformEvents.VALIDATE_COMPLETED)
                .and()
            
            // Plan completed -> Apply
            .withExternal()
                .source(TerraformStates.TERRAFORM_PLAN)
                .target(TerraformStates.TERRAFORM_APPLY)
                .event(TerraformEvents.PLAN_COMPLETED)
                .and()
            
            // Apply completed -> Output
            .withExternal()
                .source(TerraformStates.TERRAFORM_APPLY)
                .target(TerraformStates.TERRAFORM_OUTPUT)
                .event(TerraformEvents.APPLY_COMPLETED)
                .and()
            
            // Output completed -> Completed
            .withExternal()
                .source(TerraformStates.TERRAFORM_OUTPUT)
                .target(TerraformStates.COMPLETED)
                .event(TerraformEvents.OUTPUT_COMPLETED)
                .and()
            
            // Error handling - from any state to ERROR
            .withExternal()
                .source(TerraformStates.WORKSPACE_NAME_GENERATION)
                .target(TerraformStates.ERROR)
                .event(TerraformEvents.ERROR_OCCURRED)
                .and()
            .withExternal()
                .source(TerraformStates.TERRAFORM_INIT)
                .target(TerraformStates.ERROR)
                .event(TerraformEvents.ERROR_OCCURRED)
                .and()
            .withExternal()
                .source(TerraformStates.TERRAFORM_VALIDATE)
                .target(TerraformStates.ERROR)
                .event(TerraformEvents.ERROR_OCCURRED)
                .and()
            .withExternal()
                .source(TerraformStates.TERRAFORM_PLAN)
                .target(TerraformStates.ERROR)
                .event(TerraformEvents.ERROR_OCCURRED)
                .and()
            .withExternal()
                .source(TerraformStates.TERRAFORM_APPLY)
                .target(TerraformStates.ERROR)
                .event(TerraformEvents.ERROR_OCCURRED)
                .and()
            .withExternal()
                .source(TerraformStates.TERRAFORM_OUTPUT)
                .target(TerraformStates.ERROR)
                .event(TerraformEvents.ERROR_OCCURRED);
    }
}

