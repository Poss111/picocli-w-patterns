# Architecture Diagrams

This document contains class diagrams and flow diagrams for the Workflow CLI application, illustrating the design patterns used.

## Table of Contents
- [Overall Class Diagram](#overall-class-diagram)
- [Workflow 1 Flow (Chain of Responsibility + Factory + Strategy)](#workflow-1-flow-chain-of-responsibility--factory--strategy)
- [Workflow 2 Flow (Template Method with Error Handling)](#workflow-2-flow-template-method-with-error-handling)
- [Error Handling Flow (Workflow 2)](#error-handling-flow-workflow-2)
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
        #TerraformContext context
        +executeWorkflow(String, String, TerraformContext) boolean
        #printHeader()
        #printFooter(boolean)
        #init(TerraformContext) boolean*
        #validate(TerraformContext) boolean*
        #plan(TerraformContext) boolean*
        #apply(TerraformContext) boolean*
        #output(TerraformContext) boolean*
    }
    
    class Workflow2TerraformTemplate {
        #printHeader()
        #init(TerraformContext) boolean
        #validate(TerraformContext) boolean
        #plan(TerraformContext) boolean
        #apply(TerraformContext) boolean
        #output(TerraformContext) boolean
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
    TerraformWorkflowTemplate --> TerraformContext : uses
    Workflow2Command --> TerraformContext : creates
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

## Workflow 2 Flow (Template Method with Error Handling + Context)

This sequence diagram shows the execution flow of Workflow 2 using the Template Method pattern with comprehensive error handling and context passing.

```mermaid
sequenceDiagram
    participant User
    participant W2 as Workflow2Command
    participant Ctx as TerraformContext
    participant Template as TerraformWorkflowTemplate
    participant Concrete as Workflow2TerraformTemplate
    
    User->>W2: run()
    W2->>Ctx: new TerraformContext(workflowName, timeToRun)
    W2->>Ctx: setAttribute("environment", "production")
    W2->>Ctx: setAttribute("region", "us-east-1")
    W2->>Template: setMaxRetries(2)
    W2->>Template: enableFailureSimulation(stage, attempts)
    W2->>Template: executeWorkflow(workflowName, timeToRun, context)
    
    Note over Template: Template Method Pattern with Context
    Template->>Template: printHeader()
    
    Note over Template,Concrete: Error Handling with Retry + Context Passing
    loop For each stage
        Template->>Template: executeWithRetry(stage)
        Template->>Concrete: Execute stage with context<br/>(init/validate/plan/apply/output)
        
        Note over Concrete: Stage uses context
        Concrete->>Ctx: Verify prerequisites (e.g., providers_initialized)
        Concrete->>Ctx: Execute stage logic
        Concrete->>Ctx: Store results (e.g., vpc_id, resource counts)
        
        alt Stage Succeeds
            Concrete-->>Template: return true
        else Stage Fails (Missing Prerequisites)
            Concrete-->>Template: return false (validation failed)
            Template->>Template: handleError(exception)
            Template->>Concrete: cleanup(exception)
            Template->>Template: printFooter(false)
            Template-->>W2: return false
        else Stage Fails (Exception)
            Concrete-->>Template: throw exception
            Template->>Template: Check if recoverable
            
            alt Recoverable & Retries Left
                Template->>Concrete: onStageError(exception, attempt)
                Template->>Template: Wait (exponential backoff)
                Template->>Concrete: Retry stage with context
            else Not Recoverable or Max Retries
                Template->>Template: handleError(exception)
                Template->>Concrete: cleanup(exception)
                Template->>Template: printFooter(false)
                Template-->>W2: return false
            end
        end
    end
    
    Template->>Template: printFooter(true)
    Template-->>W2: return true
    W2->>Ctx: getAttributeKeys() (display final state)
    W2-->>User: Workflow completed
```

---

## Error Handling Flow (Workflow 2)

This flowchart shows the error handling and retry logic in the Template Method pattern.

```mermaid
flowchart TD
    Start([Execute Stage]) --> Try[Try Execute Stage]
    Try --> Success{Success?}
    
    Success -->|Yes| Next([Continue to Next Stage])
    Success -->|No| Exception[Exception Thrown]
    
    Exception --> Recoverable{Recoverable<br/>Error?}
    
    Recoverable -->|No| HandleError[handleError hook]
    HandleError --> Cleanup[cleanup hook]
    Cleanup --> FailEnd([Workflow Failed])
    
    Recoverable -->|Yes| CheckRetries{Retries<br/>Left?}
    
    CheckRetries -->|No| HandleError
    CheckRetries -->|Yes| ErrorHook[onStageError hook]
    
    ErrorHook --> Wait[Wait with<br/>Exponential Backoff<br/>2s → 4s → 6s]
    Wait --> Retry[Retry Stage]
    Retry --> Try
    
    style Start fill:#e1f5ff,stroke:#333,stroke-width:2px
    style Next fill:#d4edda,stroke:#333,stroke-width:2px
    style FailEnd fill:#f8d7da,stroke:#333,stroke-width:2px
    style Success fill:#fff3cd,stroke:#333,stroke-width:2px
    style Recoverable fill:#fff3cd,stroke:#333,stroke-width:2px
    style CheckRetries fill:#fff3cd,stroke:#333,stroke-width:2px
    style ErrorHook fill:#d1ecf1,stroke:#333,stroke-width:2px
    style Cleanup fill:#f8d7da,stroke:#333,stroke-width:2px
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
   - **Enhancement**: Context passing for shared state between stages

### Pattern Composition

Workflow 1 demonstrates how multiple patterns work together:
- **WorkspaceNameHandler** (part of Chain of Responsibility) uses **WorkspaceNameFactory** (Factory pattern) to create a **WorkspaceNameStrategy** (Strategy pattern)
- **TerraformContext** is passed through the entire chain, allowing handlers to share state
- **Input Validation** ensures each handler has required data before execution
- This shows how design patterns compose to create flexible, maintainable solutions

### Context Object Benefits

The `TerraformContext` is used by both patterns and provides:
1. **Shared State**: Handlers/stages can store and retrieve data
2. **Type Safety**: Generic `getAttribute()` with type parameter
3. **Validation**: Automatic checking of required attributes (Chain of Responsibility)
4. **Prerequisite Checking**: Stages verify required data exists (Template Method)
5. **Traceability**: Clear view of all attributes at any stage
6. **Extensibility**: Easy to add new attributes without changing signatures
7. **Pattern Agnostic**: Same context class works with different patterns

---

## Exception Hierarchy (Workflow 2)

This diagram shows the custom exception hierarchy for error handling in the Template Method pattern.

```mermaid
classDiagram
    class Exception {
        <<Java Built-in>>
    }
    
    class TerraformWorkflowException {
        -String stage
        -boolean recoverable
        +getStage() String
        +isRecoverable() boolean
        +toString() String
    }
    
    class InitializationException {
        +InitializationException(String)
        +InitializationException(String, Throwable)
    }
    
    class ValidationException {
        +ValidationException(String)
        +ValidationException(String, Throwable)
    }
    
    class PlanningException {
        +PlanningException(String)
        +PlanningException(String, Throwable)
    }
    
    class ApplyException {
        +ApplyException(String)
        +ApplyException(String, Throwable)
    }
    
    class OutputException {
        +OutputException(String)
        +OutputException(String, Throwable)
    }
    
    Exception <|-- TerraformWorkflowException
    TerraformWorkflowException <|-- InitializationException : recoverable=true
    TerraformWorkflowException <|-- ValidationException : recoverable=false
    TerraformWorkflowException <|-- PlanningException : recoverable=true
    TerraformWorkflowException <|-- ApplyException : recoverable=false
    TerraformWorkflowException <|-- OutputException : recoverable=true
    
    note for TerraformWorkflowException "Base exception with<br/>stage context and<br/>recoverable flag"
    note for InitializationException "Recoverable<br/>Retries allowed"
    note for ValidationException "Non-recoverable<br/>Fails immediately"
```

