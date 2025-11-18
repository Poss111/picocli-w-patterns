# Architecture Diagrams

This document contains class diagrams and flow diagrams for the Workflow CLI application, illustrating the design patterns used.

## Table of Contents
- [Overall Class Diagram](#overall-class-diagram)
- [Workflow 1 Flow (Chain of Responsibility + Factory + Strategy)](#workflow-1-flow-chain-of-responsibility--factory--strategy)
- [Workflow 2 Flow (Template Method)](#workflow-2-flow-template-method)
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
    
    %% Chain of Responsibility Pattern
    class TerraformHandler {
        <<abstract>>
        #TerraformHandler nextHandler
        +setNext(TerraformHandler) TerraformHandler
        +handle(String, String) boolean
        #passToNext(String, String) boolean
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

## Workflow 1 Flow (Chain of Responsibility + Factory + Strategy)

This sequence diagram shows the execution flow of Workflow 1, demonstrating how the Chain of Responsibility, Factory, and Strategy patterns work together.

```mermaid
sequenceDiagram
    participant User
    participant W1 as Workflow1Command
    participant WNH as WorkspaceNameHandler
    participant Factory as WorkspaceNameFactory
    participant Strategy as WorkspaceNameStrategy
    participant Init as TerraformInitHandler
    participant Validate as TerraformValidateHandler
    participant Plan as TerraformPlanHandler
    participant Apply as TerraformApplyHandler
    participant Output as TerraformOutputHandler
    
    User->>W1: run()
    W1->>W1: Create handler instances
    W1->>WNH: Create with strategyType & parameter
    W1->>W1: Chain handlers together
    W1->>WNH: handle(workflowName, timeToRun)
    
    Note over WNH,Strategy: Factory Pattern
    WNH->>Factory: createStrategy(strategyType, parameter)
    Factory->>Strategy: new Strategy()
    Factory-->>WNH: return strategy instance
    
    Note over WNH,Strategy: Strategy Pattern
    WNH->>Strategy: generateWorkspaceName(workflowName, timeToRun)
    Strategy-->>WNH: return workspace name
    WNH->>WNH: Print workspace name
    
    Note over WNH,Output: Chain of Responsibility
    WNH->>Init: handle(workflowName, timeToRun)
    Init->>Init: Execute init stage
    Init->>Validate: handle(workflowName, timeToRun)
    Validate->>Validate: Execute validate stage
    Validate->>Plan: handle(workflowName, timeToRun)
    Plan->>Plan: Execute plan stage
    Plan->>Apply: handle(workflowName, timeToRun)
    Apply->>Apply: Execute apply stage
    Apply->>Output: handle(workflowName, timeToRun)
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

## Design Patterns Summary

### Patterns Used

1. **Chain of Responsibility** (Workflow 1)
   - Handlers: `TerraformHandler` (abstract), `WorkspaceNameHandler`, `TerraformInitHandler`, `TerraformValidateHandler`, `TerraformPlanHandler`, `TerraformApplyHandler`, `TerraformOutputHandler`
   - Purpose: Process Terraform stages sequentially, stopping on failure

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

### Pattern Composition

Workflow 1 demonstrates how multiple patterns work together:
- **WorkspaceNameHandler** (part of Chain of Responsibility) uses **WorkspaceNameFactory** (Factory pattern) to create a **WorkspaceNameStrategy** (Strategy pattern)
- This shows how design patterns compose to create flexible, maintainable solutions

