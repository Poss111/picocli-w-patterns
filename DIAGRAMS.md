# Architecture Diagrams

This document contains class diagrams and flow diagrams for the Workflow CLI application, illustrating the design patterns used.

## Table of Contents
- [Overall Class Diagram](#overall-class-diagram)
- [Workflow 1 Flow (Chain of Responsibility + Factory + Strategy)](#workflow-1-flow-chain-of-responsibility--factory--strategy)
- [Workflow 2 Flow (Template Method)](#workflow-2-flow-template-method)
- [Workflow 4 Flow (Spring State Machine)](#workflow-4-flow-spring-state-machine)
- [State Machine State Diagram](#state-machine-state-diagram)
- [Factory Pattern Detail](#factory-pattern-detail)
- [Strategy Pattern Detail](#strategy-pattern-detail)

---

## Overall Class Diagram

This diagram shows all the main classes and their relationships across all design patterns.

```mermaid
classDiagram
    %% Commands
    class Workflow1Command {
        -String workflowName
        -String timeToRun
        -StrategyType strategyType
        -String strategyParameter
        +run()
    }
    
    class Workflow2Command {
        -String workflowName
        -String timeToRun
        +run()
    }
    
    class Workflow3Command {
        -String workflowName
        -String timeToRun
        +run()
    }
    
    %% Context Class
    class TerraformContext {
        -String workflowName
        -String timeToRun
        -Map~String,Object~ attributes
        +setAttribute(String, Object)
        +getAttribute(String) Object
        +hasAttribute(String) boolean
        +getAttributeKeys() Set~String~
    }
    
    %% Chain of Responsibility Pattern
    class TerraformHandler {
        <<abstract>>
        #TerraformHandler nextHandler
        +setNext(TerraformHandler) TerraformHandler
        +handle(TerraformContext) boolean
        #doHandle(TerraformContext) boolean*
        #getRequiredAttributes() Set~String~
        #passToNext(TerraformContext) boolean
        #requireAttributes(String...) Set~String~
    }
    
    class WorkspaceNameHandler {
        -StrategyType strategyType
        -String strategyParameter
        -String generatedWorkspaceName
        +handle(String, String) boolean
        +getGeneratedWorkspaceName() String
    }
    
    class TerraformInitHandler {
        +handle(String, String) boolean
    }
    
    class TerraformValidateHandler {
        +handle(String, String) boolean
    }
    
    class TerraformPlanHandler {
        +handle(String, String) boolean
    }
    
    class TerraformApplyHandler {
        +handle(String, String) boolean
    }
    
    class TerraformOutputHandler {
        +handle(String, String) boolean
    }
    
    %% Factory Pattern
    class WorkspaceNameFactory {
        <<factory>>
        +createStrategy(StrategyType, String)$ WorkspaceNameStrategy
        +createStrategy(StrategyType)$ WorkspaceNameStrategy
    }
    
    class StrategyType {
        <<enumeration>>
        ENVIRONMENT
        TIMESTAMP
        CUSTOM_PREFIX
        SIMPLE
    }
    
    %% Strategy Pattern
    class WorkspaceNameStrategy {
        <<interface>>
        +generateWorkspaceName(String, String) String
        +getStrategyDescription() String
    }
    
    class EnvironmentBasedStrategy {
        -String environment
        +generateWorkspaceName(String, String) String
        +getStrategyDescription() String
    }
    
    class TimestampBasedStrategy {
        +generateWorkspaceName(String, String) String
        +getStrategyDescription() String
    }
    
    class CustomPrefixStrategy {
        -String prefix
        +generateWorkspaceName(String, String) String
        +getStrategyDescription() String
    }
    
    class SimpleStrategy {
        +generateWorkspaceName(String, String) String
        +getStrategyDescription() String
    }
    
    %% Template Method Pattern
    class TerraformWorkflowTemplate {
        <<abstract>>
        #String workflowName
        #String timeToRun
        +executeWorkflow(String, String) boolean
        #printHeader()
        #printFooter(boolean)
        #init() boolean*
        #validate() boolean*
        #plan() boolean*
        #apply() boolean*
        #output() boolean*
    }
    
    class Workflow2TerraformTemplate {
        #printHeader()
        #init() boolean
        #validate() boolean
        #plan() boolean
        #apply() boolean
        #output() boolean
    }
    
    %% Relationships - Chain of Responsibility
    TerraformHandler <|-- WorkspaceNameHandler
    TerraformHandler <|-- TerraformInitHandler
    TerraformHandler <|-- TerraformValidateHandler
    TerraformHandler <|-- TerraformPlanHandler
    TerraformHandler <|-- TerraformApplyHandler
    TerraformHandler <|-- TerraformOutputHandler
    
    TerraformHandler o-- TerraformHandler : next
    TerraformHandler --> TerraformContext : uses
    
    %% Relationships - Commands
    Workflow1Command --> WorkspaceNameHandler : creates
    Workflow1Command --> TerraformInitHandler : creates
    Workflow1Command --> TerraformValidateHandler : creates
    Workflow1Command --> TerraformPlanHandler : creates
    Workflow1Command --> TerraformApplyHandler : creates
    Workflow1Command --> TerraformOutputHandler : creates
    
    Workflow2Command --> Workflow2TerraformTemplate : creates
    
    %% Relationships - Factory & Strategy
    WorkspaceNameHandler --> WorkspaceNameFactory : uses
    WorkspaceNameFactory --> WorkspaceNameStrategy : creates
    WorkspaceNameFactory --> StrategyType : uses
    
    WorkspaceNameStrategy <|.. EnvironmentBasedStrategy : implements
    WorkspaceNameStrategy <|.. TimestampBasedStrategy : implements
    WorkspaceNameStrategy <|.. CustomPrefixStrategy : implements
    WorkspaceNameStrategy <|.. SimpleStrategy : implements
    
    WorkspaceNameHandler --> WorkspaceNameStrategy : uses
    
    %% Relationships - Template Method
    TerraformWorkflowTemplate <|-- Workflow2TerraformTemplate
```

---

## Workflow 1 Flow (Chain of Responsibility + Factory + Strategy + Context)

This sequence diagram shows the execution flow of Workflow 1, demonstrating how the Chain of Responsibility, Factory, Strategy patterns work together with context passing and validation.

```mermaid
sequenceDiagram
    participant User
    participant W1 as Workflow1Command
    participant Ctx as TerraformContext
    participant WNH as WorkspaceNameHandler
    participant Factory as WorkspaceNameFactory
    participant Strategy as WorkspaceNameStrategy
    participant Init as TerraformInitHandler
    participant Validate as TerraformValidateHandler
    participant Plan as TerraformPlanHandler
    participant Apply as TerraformApplyHandler
    participant Output as TerraformOutputHandler
    
    User->>W1: run()
    W1->>Ctx: new TerraformContext(workflowName, timeToRun)
    W1->>Ctx: setAttribute("environment", param)
    W1->>W1: Create handler instances
    W1->>WNH: Create with strategyType & parameter
    W1->>W1: Chain handlers together
    W1->>WNH: handle(context)
    
    Note over WNH: No validation (no requirements)
    Note over WNH,Strategy: Factory Pattern
    WNH->>Factory: createStrategy(strategyType, parameter)
    Factory->>Strategy: new Strategy()
    Factory-->>WNH: return strategy instance
    
    Note over WNH,Strategy: Strategy Pattern
    WNH->>Strategy: generateWorkspaceName()
    Strategy-->>WNH: return workspace name
    WNH->>Ctx: setAttribute("workspace_name", name)
    
    Note over WNH,Output: Chain of Responsibility with Context
    WNH->>Init: handle(context)
    Init->>Init: Validate: requires "workspace_name"
    Init->>Init: Execute init stage
    Init->>Ctx: setAttribute("terraform_initialized", true)
    
    Init->>Validate: handle(context)
    Validate->>Validate: Validate: requires "terraform_initialized"
    Validate->>Validate: Execute validate stage
    Validate->>Ctx: setAttribute("configuration_valid", true)
    
    Validate->>Plan: handle(context)
    Plan->>Plan: Validate: requires "configuration_valid"
    Plan->>Plan: Execute plan stage
    Plan->>Ctx: setAttribute("plan_created", true)
    
    Plan->>Apply: handle(context)
    Apply->>Apply: Validate: requires "plan_created"
    Apply->>Apply: Execute apply stage
    Apply->>Ctx: setAttribute("apply_complete", true)
    
    Apply->>Output: handle(context)
    Output->>Output: Validate: requires "apply_complete"
    Output->>Ctx: getAttribute("workspace_name")
    Output->>Ctx: getAttribute("instance_id")
    Output->>Output: Execute output stage
    
    Output-->>Apply: return success
    Apply-->>Plan: return success
    Plan-->>Validate: return success
    Validate-->>Init: return success
    Init-->>WNH: return success
    WNH-->>W1: return success
    
    W1->>W1: Print completion status
    W1-->>User: Pipeline completed
```

---

## Workflow 2 Flow (Template Method)

This sequence diagram shows the execution flow of Workflow 2 using the Template Method pattern.

```mermaid
sequenceDiagram
    participant User
    participant W2 as Workflow2Command
    participant Template as Workflow2TerraformTemplate
    
    User->>W2: run()
    W2->>Template: new Workflow2TerraformTemplate()
    W2->>Template: executeWorkflow(workflowName, timeToRun)
    
    Note over Template: Template Method Pattern
    Template->>Template: printHeader()
    
    Note over Template: Fixed Algorithm Structure
    Template->>Template: init()
    Note right of Template: Stage 1/5<br/>Initialize Terraform
    
    Template->>Template: validate()
    Note right of Template: Stage 2/5<br/>Validate configuration
    
    Template->>Template: plan()
    Note right of Template: Stage 3/5<br/>Create execution plan
    
    Template->>Template: apply()
    Note right of Template: Stage 4/5<br/>Apply changes
    
    Template->>Template: output()
    Note right of Template: Stage 5/5<br/>Display outputs
    
    Template->>Template: printFooter(success)
    Template-->>W2: return success
    
    W2-->>User: Workflow completed
```

---

## Workflow 4 Flow (Spring State Machine)

This diagram shows the execution flow of Workflow 4 using Spring State Machine pattern.

```mermaid
sequenceDiagram
    participant User
    participant W4 as Workflow4Command
    participant SM as StateMachine
    participant Ctx as TerraformContext
    participant A1 as WorkspaceNameAction
    participant A2 as InitAction
    participant A3 as ValidateAction
    participant A4 as PlanAction
    participant A5 as ApplyAction
    participant A6 as OutputAction
    
    User->>W4: run()
    W4->>Ctx: new TerraformContext()
    W4->>SM: Create StateMachine
    W4->>SM: Store context in extended state
    W4->>SM: start()
    W4->>SM: sendEvent(START_WORKFLOW)
    
    Note over SM,A1: State Machine Pattern
    SM->>SM: Transition to WORKSPACE_NAME_GENERATION
    SM->>A1: execute()
    A1->>Ctx: setAttribute("workspace_name", name)
    A1->>SM: sendEvent(WORKSPACE_NAME_GENERATED)
    
    SM->>SM: Transition to TERRAFORM_INIT
    SM->>A2: execute()
    A2->>Ctx: getAttribute("workspace_name")
    A2->>Ctx: setAttribute("terraform_initialized", true)
    A2->>SM: sendEvent(INIT_COMPLETED)
    
    SM->>SM: Transition to TERRAFORM_VALIDATE
    SM->>A3: execute()
    A3->>Ctx: setAttribute("configuration_valid", true)
    A3->>SM: sendEvent(VALIDATE_COMPLETED)
    
    SM->>SM: Transition to TERRAFORM_PLAN
    SM->>A4: execute()
    A4->>Ctx: setAttribute("plan_created", true)
    A4->>SM: sendEvent(PLAN_COMPLETED)
    
    SM->>SM: Transition to TERRAFORM_APPLY
    SM->>A5: execute()
    A5->>Ctx: setAttribute("apply_complete", true)
    A5->>SM: sendEvent(APPLY_COMPLETED)
    
    SM->>SM: Transition to TERRAFORM_OUTPUT
    SM->>A6: execute()
    A6->>Ctx: getAttribute("instance_id")
    A6->>SM: sendEvent(OUTPUT_COMPLETED)
    
    SM->>SM: Transition to COMPLETED
    SM-->>W4: State is COMPLETED
    W4->>SM: stop()
    W4-->>User: Workflow completed
```

---

## State Machine State Diagram

This state diagram shows all possible states and transitions in Workflow 4.

```mermaid
stateDiagram-v2
    [*] --> INITIAL
    INITIAL --> WORKSPACE_NAME_GENERATION : START_WORKFLOW
    WORKSPACE_NAME_GENERATION --> TERRAFORM_INIT : WORKSPACE_NAME_GENERATED
    TERRAFORM_INIT --> TERRAFORM_VALIDATE : INIT_COMPLETED
    TERRAFORM_VALIDATE --> TERRAFORM_PLAN : VALIDATE_COMPLETED
    TERRAFORM_PLAN --> TERRAFORM_APPLY : PLAN_COMPLETED
    TERRAFORM_APPLY --> TERRAFORM_OUTPUT : APPLY_COMPLETED
    TERRAFORM_OUTPUT --> COMPLETED : OUTPUT_COMPLETED
    COMPLETED --> [*]
    
    WORKSPACE_NAME_GENERATION --> ERROR : ERROR_OCCURRED
    TERRAFORM_INIT --> ERROR : ERROR_OCCURRED
    TERRAFORM_VALIDATE --> ERROR : ERROR_OCCURRED
    TERRAFORM_PLAN --> ERROR : ERROR_OCCURRED
    TERRAFORM_APPLY --> ERROR : ERROR_OCCURRED
    TERRAFORM_OUTPUT --> ERROR : ERROR_OCCURRED
    ERROR --> [*]
    
    note right of WORKSPACE_NAME_GENERATION
        Action: Generate workspace name
        using Factory pattern
    end note
    
    note right of TERRAFORM_INIT
        Action: Initialize Terraform
        Requires: workspace_name
    end note
    
    note right of COMPLETED
        Final success state
    end note
    
    note right of ERROR
        Final error state
    end note
```

---

## Factory Pattern Detail

This diagram shows how the Factory pattern creates different strategies.

```mermaid
flowchart TD
    Start([User specifies strategy type]) --> Factory{WorkspaceNameFactory}
    
    Factory -->|ENVIRONMENT| CheckEnv{Parameter provided?}
    CheckEnv -->|Yes| CreateEnv[Create EnvironmentBasedStrategy]
    CheckEnv -->|No| Error1[Throw IllegalArgumentException]
    
    Factory -->|TIMESTAMP| CreateTime[Create TimestampBasedStrategy]
    
    Factory -->|CUSTOM_PREFIX| CheckPrefix{Parameter provided?}
    CheckPrefix -->|Yes| CreatePrefix[Create CustomPrefixStrategy]
    CheckPrefix -->|No| Error2[Throw IllegalArgumentException]
    
    Factory -->|SIMPLE| CreateSimple[Create SimpleStrategy]
    
    CreateEnv --> Return[Return WorkspaceNameStrategy]
    CreateTime --> Return
    CreatePrefix --> Return
    CreateSimple --> Return
    
    Return --> Use[Strategy used to generate workspace name]
    Error1 --> End([End with error])
    Error2 --> End
    Use --> End2([End successfully])
    
    style Factory fill:#f9f,stroke:#333,stroke-width:2px
    style Return fill:#9f9,stroke:#333,stroke-width:2px
    style Error1 fill:#f99,stroke:#333,stroke-width:2px
    style Error2 fill:#f99,stroke:#333,stroke-width:2px
```

---

## Strategy Pattern Detail

This diagram shows how different strategies generate workspace names.

```mermaid
flowchart LR
    Input[/"Input:<br/>workflowName='Production Deploy'<br/>timeToRun='now'"/]
    
    Input --> S1[EnvironmentBasedStrategy<br/>parameter='production']
    Input --> S2[TimestampBasedStrategy]
    Input --> S3[CustomPrefixStrategy<br/>parameter='team-alpha']
    Input --> S4[SimpleStrategy]
    
    S1 --> O1[/"Output:<br/>production-deploy-production"/]
    S2 --> O2[/"Output:<br/>production-deploy-20241118-143022"/]
    S3 --> O3[/"Output:<br/>team-alpha-production-deploy"/]
    S4 --> O4[/"Output:<br/>production-deploy"/]
    
    style Input fill:#e1f5ff,stroke:#333,stroke-width:2px
    style O1 fill:#d4edda,stroke:#333,stroke-width:2px
    style O2 fill:#d4edda,stroke:#333,stroke-width:2px
    style O3 fill:#d4edda,stroke:#333,stroke-width:2px
    style O4 fill:#d4edda,stroke:#333,stroke-width:2px
    style S1 fill:#fff3cd,stroke:#333,stroke-width:2px
    style S2 fill:#fff3cd,stroke:#333,stroke-width:2px
    style S3 fill:#fff3cd,stroke:#333,stroke-width:2px
    style S4 fill:#fff3cd,stroke:#333,stroke-width:2px
```

---

## Chain of Responsibility Visualization

This diagram shows the complete handler chain in Workflow 1.

```mermaid
flowchart LR
    Start([Start]) --> H0[WorkspaceNameHandler<br/>Stage 0/5]
    H0 -->|success| H1[TerraformInitHandler<br/>Stage 1/5]
    H1 -->|success| H2[TerraformValidateHandler<br/>Stage 2/5]
    H2 -->|success| H3[TerraformPlanHandler<br/>Stage 3/5]
    H3 -->|success| H4[TerraformApplyHandler<br/>Stage 4/5]
    H4 -->|success| H5[TerraformOutputHandler<br/>Stage 5/5]
    H5 -->|success| Success([Pipeline Success])
    
    H0 -->|failure| Fail([Pipeline Failed])
    H1 -->|failure| Fail
    H2 -->|failure| Fail
    H3 -->|failure| Fail
    H4 -->|failure| Fail
    H5 -->|failure| Fail
    
    style Start fill:#e1f5ff,stroke:#333,stroke-width:2px
    style Success fill:#d4edda,stroke:#333,stroke-width:2px
    style Fail fill:#f8d7da,stroke:#333,stroke-width:2px
    style H0 fill:#fff3cd,stroke:#333,stroke-width:2px
    style H1 fill:#d1ecf1,stroke:#333,stroke-width:2px
    style H2 fill:#d1ecf1,stroke:#333,stroke-width:2px
    style H3 fill:#d1ecf1,stroke:#333,stroke-width:2px
    style H4 fill:#d1ecf1,stroke:#333,stroke-width:2px
    style H5 fill:#d1ecf1,stroke:#333,stroke-width:2px
```

---

---

## Context and Validation Flow

This diagram shows how context attributes are set and validated through the chain.

```mermaid
flowchart TD
    Start([Start: Create Context]) --> WSH[WorkspaceNameHandler]
    WSH -->|Sets: workspace_name| Init[TerraformInitHandler]
    
    Init -->|Validates: workspace_name| InitCheck{Validation}
    InitCheck -->|✓ Pass| InitExec[Execute Init]
    InitCheck -->|✗ Fail| Error[Return False]
    InitExec -->|Sets: terraform_initialized<br/>provider_version| Val[TerraformValidateHandler]
    
    Val -->|Validates: terraform_initialized| ValCheck{Validation}
    ValCheck -->|✓ Pass| ValExec[Execute Validate]
    ValCheck -->|✗ Fail| Error
    ValExec -->|Sets: configuration_valid| Plan[TerraformPlanHandler]
    
    Plan -->|Validates: configuration_valid<br/>workspace_name| PlanCheck{Validation}
    PlanCheck -->|✓ Pass| PlanExec[Execute Plan]
    PlanCheck -->|✗ Fail| Error
    PlanExec -->|Sets: plan_created<br/>resources_to_add<br/>resources_to_change| Apply[TerraformApplyHandler]
    
    Apply -->|Validates: plan_created| ApplyCheck{Validation}
    ApplyCheck -->|✓ Pass| ApplyExec[Execute Apply]
    ApplyCheck -->|✗ Fail| Error
    ApplyExec -->|Sets: apply_complete<br/>instance_id<br/>security_group_id| Output[TerraformOutputHandler]
    
    Output -->|Validates: apply_complete| OutputCheck{Validation}
    OutputCheck -->|✓ Pass| OutputExec[Execute Output]
    OutputCheck -->|✗ Fail| Error
    OutputExec -->|Reads all attributes| Success([Success])
    
    style Start fill:#e1f5ff,stroke:#333,stroke-width:2px
    style Success fill:#d4edda,stroke:#333,stroke-width:2px
    style Error fill:#f8d7da,stroke:#333,stroke-width:2px
    style InitCheck fill:#fff3cd,stroke:#333,stroke-width:2px
    style ValCheck fill:#fff3cd,stroke:#333,stroke-width:2px
    style PlanCheck fill:#fff3cd,stroke:#333,stroke-width:2px
    style ApplyCheck fill:#fff3cd,stroke:#333,stroke-width:2px
    style OutputCheck fill:#fff3cd,stroke:#333,stroke-width:2px
```

---

## Design Patterns Summary

### Patterns Used

1. **Chain of Responsibility** (Workflow 1)
   - Handlers: `TerraformHandler` (abstract), `WorkspaceNameHandler`, `TerraformInitHandler`, `TerraformValidateHandler`, `TerraformPlanHandler`, `TerraformApplyHandler`, `TerraformOutputHandler`
   - Purpose: Process Terraform stages sequentially, stopping on failure
   - **Enhancement**: Context passing and input validation

2. **Factory Pattern** (Workflow 1)
   - Factory: `WorkspaceNameFactory`
   - Purpose: Create appropriate workspace naming strategy based on user input

3. **Strategy Pattern** (Workflow 1)
   - Interface: `WorkspaceNameStrategy`
   - Strategies: `EnvironmentBasedStrategy`, `TimestampBasedStrategy`, `CustomPrefixStrategy`, `SimpleStrategy`
   - Purpose: Allow runtime selection of workspace naming algorithm

4. **Template Method Pattern** (Workflow 2)
   - Template: `TerraformWorkflowTemplate` (abstract)
   - Concrete: `Workflow2TerraformTemplate`
   - Purpose: Define fixed algorithm structure with customizable steps

5. **State Pattern** (Workflow 4)
   - States: `TerraformStates` enum
   - Events: `TerraformEvents` enum
   - Actions: One action class per state
   - Configuration: `TerraformStateMachineConfig`
   - Purpose: Model workflow as state transitions

### Pattern Composition

Workflow 1 demonstrates how multiple patterns work together:
- **WorkspaceNameHandler** (part of Chain of Responsibility) uses **WorkspaceNameFactory** (Factory pattern) to create a **WorkspaceNameStrategy** (Strategy pattern)
- **TerraformContext** is passed through the entire chain, allowing handlers to share state
- **Input Validation** ensures each handler has required data before execution
- This shows how design patterns compose to create flexible, maintainable solutions

### Context Object Benefits

The `TerraformContext` provides:
1. **Shared State**: Handlers can store and retrieve data
2. **Type Safety**: Generic `getAttribute()` with type parameter
3. **Validation**: Automatic checking of required attributes
4. **Traceability**: Clear view of all attributes at any stage
5. **Extensibility**: Easy to add new attributes without changing signatures

### Pattern Comparison

| Pattern | Workflow | Approach | Control Flow | Best For |
|---------|----------|----------|--------------|----------|
| **Chain of Responsibility** | Workflow 1 | Handlers linked together | Sequential, passes through chain | Processing pipeline with validation |
| **Template Method** | Workflow 2 | Abstract algorithm, concrete steps | Fixed sequence in template | Algorithm with customizable steps |
| **State Machine** | Workflow 4 | States + Events + Transitions | Event-driven state changes | Complex state management |

All three patterns can achieve the same result - it's about choosing the right tool for your use case!

