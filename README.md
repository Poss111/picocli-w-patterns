# Workflow CLI

A command-line interface tool built with Spring Boot and picocli for running workflows.

> 📊 **See [DIAGRAMS.md](DIAGRAMS.md) for detailed class diagrams and flow diagrams using Mermaid**

## Features

- Three workflow commands (workflow1, workflow2, workflow3)
- **Workflow 1**: Terraform deployment pipeline using **Chain of Responsibility** + **Factory** + **Strategy** patterns
  - 6 stages: Workspace Name Generation, Init, Validate, Plan, Apply, Output
  - **Context Object**: Shared state passed between handlers
  - **Input Validation**: Each handler validates required attributes before execution
  - Each handler processes its stage and passes control to the next
  - Factory pattern creates different workspace naming strategies
- **Workflow 2**: Terraform deployment pipeline using **Template Method** pattern
  - 5 stages: Init, Validate, Plan, Apply, Output
  - Abstract template defines the workflow skeleton with concrete implementations
  - **Context Object**: Shared state passed between template stages
  - **Stage Dependencies**: Each stage can verify prerequisites from context
  - **Comprehensive Error Handling**:
    - Custom exceptions for each stage
    - Automatic retry logic for recoverable errors
    - Error hooks for custom recovery
    - Cleanup/rollback hooks on failure
    - Configurable max retries
- Workflow 3: Simple workflow execution
- Each command accepts workflow name and time to run
- **Built with Spring Boot for dependency injection**
  - All handlers and templates are Spring-managed beans
  - Constructor-based dependency injection for testability
  - Factory beans for runtime object creation
- Uses picocli for elegant command-line parsing
- Demonstrates Gang of Four design patterns (Chain of Responsibility, Template Method, Factory, Strategy)

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Building the Project

```bash
mvn clean package
```

## Running the Application

### Display Help

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--help"
```

### Run Workflow 1 (Terraform Pipeline with Factory)

Workflow 1 executes a full Terraform deployment pipeline through 6 stages, using Factory pattern for workspace naming:

```bash
# Default (timestamp-based workspace name)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow1 --name 'Infrastructure Deployment' --time 'now'"

# Environment-based workspace name
mvn spring-boot:run -Dspring-boot.run.arguments="workflow1 -n 'Production Deploy' -t 'now' -s ENVIRONMENT -p production"

# Custom prefix workspace name
mvn spring-boot:run -Dspring-boot.run.arguments="workflow1 -n 'VPC Setup' -t 'now' -s CUSTOM_PREFIX -p team-alpha"

# Simple workspace name
mvn spring-boot:run -Dspring-boot.run.arguments="workflow1 -n 'Network Setup' -t 'now' -s SIMPLE"
```

**Workspace Naming Strategies:**
- `TIMESTAMP` (default): Generates names like `infrastructure-deployment-20241118-143022`
- `ENVIRONMENT`: Generates names like `production-deploy-production` (requires `-p` parameter)
- `CUSTOM_PREFIX`: Generates names like `team-alpha-vpc-setup` (requires `-p` parameter)
- `SIMPLE`: Generates names like `network-setup` (sanitized workflow name)

This will execute:
0. **Workspace Name Generation** - Use Factory to create naming strategy and generate workspace name
1. **Terraform Init** - Initialize working directory and download providers
2. **Terraform Validate** - Validate configuration files
3. **Terraform Plan** - Create execution plan
4. **Terraform Apply** - Apply infrastructure changes
5. **Terraform Output** - Display output values

### Run Workflow 2 (Terraform Template with Error Handling)

Workflow 2 executes the same Terraform deployment pipeline but using the Template Method pattern with comprehensive error handling:

```bash
# Default (2 retries for recoverable errors)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 --name 'Network Infrastructure' --time 'now'"

# With custom retry count
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Production Deploy' -t 'now' -r 3"

# No retries
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Dev Deploy' -t 'now' --retries 0"

# Test error handling with simulated failures
# Simulate Init failure (recoverable - will retry)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure INIT"

# Simulate Validate failure (non-recoverable - fails immediately)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure VALIDATE"

# Simulate Plan failure once, then succeed (tests retry logic)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure PLAN --failure-attempts 1"
```

This also executes:
1. **Terraform Init** - Initialize and configure backend (recoverable errors)
2. **Terraform Validate** - Check configuration syntax (non-recoverable errors)
3. **Terraform Plan** - Generate and review execution plan (recoverable errors)
4. **Terraform Apply** - Create/modify infrastructure resources (non-recoverable errors)
5. **Terraform Output** - Retrieve and display outputs (recoverable errors)

**Error Handling Features:**
- Custom exceptions for each stage type
- Automatic retry for recoverable errors (Init, Plan, Output)
- Non-recoverable errors fail immediately (Validate, Apply)
- Error hooks called before each retry
- Cleanup hooks called on final failure
- Detailed error reporting with stage information

The difference is in the **design pattern**: Template Method defines the algorithm structure in an abstract class, while concrete subclass implements each step. Error handling is built into the template.

### Run Workflow 3

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="workflow3 -n 'Report Generation' -t '14:00'"
```

## Running as Standalone JAR

After building the project, you can run it as a standalone application:

```bash
# Build the JAR
mvn clean package

# Run the JAR
java -jar target/workflow-cli-1.0.0.jar workflow1 --name "My Workflow" --time "now"
```

## Command Options

### Common Options (All Workflows)

- `-n, --name`: Name of the workflow (required)
- `-t, --time`: Time to run the workflow (required)
- `-h, --help`: Show help message
- `-V, --version`: Display version information

### Workflow 1 Additional Options

- `-s, --strategy`: Workspace naming strategy (default: TIMESTAMP)
  - `TIMESTAMP`: Adds timestamp to workspace name
  - `ENVIRONMENT`: Uses environment name (requires `-p`)
  - `CUSTOM_PREFIX`: Adds custom prefix (requires `-p`)
  - `SIMPLE`: Uses sanitized workflow name only
- `-p, --parameter`: Parameter for strategy (e.g., environment name or prefix)

### Workflow 2 Additional Options

- `-r, --retries`: Maximum number of retries for recoverable errors (default: 2)
  - Range: 0-10
  - Only applies to recoverable errors (Init, Plan, Output stages)
  - Validation and Apply errors fail immediately

**Testing/Debugging Options:**
- `--simulate-failure`: Force a failure at a specific stage for testing
  - Values: `INIT`, `VALIDATE`, `PLAN`, `APPLY`, `OUTPUT`
  - Useful for testing error handling and retry logic
- `--failure-attempts`: Number of attempts before allowing success (default: fail all)
  - Use with `--simulate-failure` to test retry behavior
  - Example: `--failure-attempts 1` fails once, then succeeds

## Examples

```bash
# Run Terraform with Chain of Responsibility + Factory patterns (workflow 1)
# Default timestamp strategy
java -jar target/workflow-cli-1.0.0.jar workflow1 -n "Production Deploy" -t "now"

# With environment-based workspace naming
java -jar target/workflow-cli-1.0.0.jar workflow1 -n "Production Deploy" -t "now" -s ENVIRONMENT -p production

# With custom prefix workspace naming
java -jar target/workflow-cli-1.0.0.jar workflow1 -n "VPC Setup" -t "now" -s CUSTOM_PREFIX -p team-alpha

# Run Terraform deployment pipeline with Template Method (workflow 2)
java -jar target/workflow-cli-1.0.0.jar workflow2 -n "Network Infrastructure" -t "now"

# Test error handling - simulate recoverable failure with retry
java -jar target/workflow-cli-1.0.0.jar workflow2 -n "Test" -t "now" --simulate-failure INIT --failure-attempts 1 -r 3

# Test error handling - simulate non-recoverable failure
java -jar target/workflow-cli-1.0.0.jar workflow2 -n "Test" -t "now" --simulate-failure VALIDATE

# Run simple workflow 3
java -jar target/workflow-cli-1.0.0.jar workflow3 -n "Monthly Cleanup" -t "01:00"
```

### Sample Output (Workflow 1)

```
═══════════════════════════════════════════════
  Workflow 1: Terraform Deployment Pipeline
  Chain of Responsibility + Factory Pattern
═══════════════════════════════════════════════
Workflow Name: Production Deploy
Scheduled Time: now
Naming Strategy: ENVIRONMENT
═══════════════════════════════════════════════

[Stage 0/5] Workspace Name Generation
─────────────────────────────
Using Factory pattern to create workspace naming strategy...
Strategy: Environment-based strategy (format: workflow-production)
Generated workspace name: production-deploy-production
✓ Workspace name determined successfully!

[Stage 1/5] Terraform Init
─────────────────────────────
Initializing Terraform working directory...
Downloading provider plugins...
✓ Terraform has been successfully initialized!

[Stage 2/5] Terraform Validate
─────────────────────────────
Validating Terraform configuration files...
Checking syntax and consistency...
✓ Configuration is valid!

[Stage 3/5] Terraform Plan
─────────────────────────────
Creating execution plan for: Production Deploy
Scheduled for: now
Analyzing resource changes...
Plan: 3 to add, 1 to change, 0 to destroy.
✓ Plan created successfully!

[Stage 4/5] Terraform Apply
─────────────────────────────
Applying Terraform changes...
Creating resources...
aws_instance.web_server: Creating...
aws_instance.web_server: Creation complete
aws_security_group.allow_http: Creating...
aws_security_group.allow_http: Creation complete
✓ Apply complete! Resources: 3 added, 1 changed, 0 destroyed.

[Stage 5/5] Terraform Output
─────────────────────────────
Retrieving output values...

Outputs:
  instance_id = "i-1234567890abcdef0"
  public_ip = "54.123.45.67"
  security_group_id = "sg-0123456789abcdef0"

✓ Workflow 'Production Deploy' completed successfully!
  All Terraform stages executed at: now

═══════════════════════════════════════════════
  ✓ Pipeline completed successfully!
═══════════════════════════════════════════════
```

### Sample Output (Workflow 2)

```
═══════════════════════════════════════════════
  Workflow 2: Terraform Template Pattern
═══════════════════════════════════════════════
Workflow Name: Network Infrastructure
Scheduled Time: now
Pattern: Template Method
═══════════════════════════════════════════════

[Stage 1/5] Terraform Init
─────────────────────────────
Initializing Terraform working directory...
Configuring backend...
Downloading required providers...
- provider registry.terraform.io/hashicorp/aws v5.0.0
- provider registry.terraform.io/hashicorp/random v3.5.0
✓ Terraform initialized successfully!

[Stage 2/5] Terraform Validate
─────────────────────────────
Validating configuration syntax...
Checking for errors in .tf files...
✓ Configuration is syntactically valid!
✓ All resource references are correct!

[Stage 3/5] Terraform Plan
─────────────────────────────
Generating execution plan for: Network Infrastructure
Reading current state...
Calculating changes...

Planned changes:
  + aws_vpc.main
  + aws_subnet.public
  + aws_internet_gateway.main
  ~ aws_route_table.public (update in-place)

Plan: 3 to add, 1 to change, 0 to destroy.
✓ Plan generated successfully!

[Stage 4/5] Terraform Apply
─────────────────────────────
Applying infrastructure changes...
This may take several minutes...

Creating resources:
  aws_vpc.main: Creating...
  aws_vpc.main: Creation complete [id=vpc-abc123]
  aws_subnet.public: Creating...
  aws_subnet.public: Creation complete [id=subnet-def456]
  aws_internet_gateway.main: Creating...
  aws_internet_gateway.main: Creation complete [id=igw-ghi789]
  aws_route_table.public: Modifying...
  aws_route_table.public: Modifications complete [id=rtb-jkl012]

✓ Apply complete!
  Resources: 3 added, 1 changed, 0 destroyed.

[Stage 5/5] Terraform Output
─────────────────────────────
Reading output values from state...

Outputs:
  vpc_id = "vpc-abc123"
  subnet_id = "subnet-def456"
  internet_gateway_id = "igw-ghi789"
  route_table_id = "rtb-jkl012"
  vpc_cidr = "10.0.0.0/16"

✓ All outputs retrieved successfully!
  Workflow 'Network Infrastructure' completed at: now

═══════════════════════════════════════════════
  ✓ Workflow completed successfully!
═══════════════════════════════════════════════
```

## Testing Error Handling (Workflow 2)

Workflow 2 includes built-in failure simulation for testing error handling and retry logic:

### Test Scenarios

**1. Test Recoverable Error with Automatic Retry**

```bash
# Simulate Init failure - will retry automatically
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure INIT -r 2"
```

Expected output:
- Init fails on first attempt
- Error hook is called
- System waits (exponential backoff)
- Retries up to 2 times
- If all retries fail, cleanup hook is called

**2. Test Non-Recoverable Error (Immediate Failure)**

```bash
# Simulate Validate failure - fails immediately (no retry)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure VALIDATE"
```

Expected output:
- Validate fails immediately
- No retry attempts (validation errors are non-recoverable)
- Cleanup hook is called
- Workflow exits with error

**3. Test Successful Retry**

```bash
# Fail once, then succeed on retry
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure PLAN --failure-attempts 1 -r 2"
```

Expected output:
- Plan fails on first attempt
- Error hook is called
- System retries
- Plan succeeds on second attempt
- Workflow continues and completes successfully

**4. Test Different Stages**

```bash
# Test Init stage (recoverable)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure INIT"

# Test Validate stage (non-recoverable)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure VALIDATE"

# Test Plan stage (recoverable)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure PLAN"

# Test Apply stage (non-recoverable)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure APPLY"

# Test Output stage (recoverable)
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure OUTPUT"
```

### Recoverable vs Non-Recoverable Errors

| Stage | Error Type | Retry Behavior |
|-------|-----------|----------------|
| **Init** | Recoverable | Automatic retry with backoff |
| **Validate** | Non-Recoverable | Fails immediately, no retry |
| **Plan** | Recoverable | Automatic retry with backoff |
| **Apply** | Non-Recoverable | Fails immediately, no retry |
| **Output** | Recoverable | Automatic retry with backoff |

**Why different error types?**
- **Recoverable**: Transient issues (network, timeouts) - worth retrying
- **Non-Recoverable**: Configuration/validation errors - retrying won't help

### Example: Testing Recoverable Error with Retry

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure PLAN --failure-attempts 1 -r 2"
```

**Expected Output:**

```
⚠ TEST MODE: Simulating failure at stage: PLAN
⚠ Failure will occur for 1 attempt(s)
═══════════════════════════════════════════════

═══════════════════════════════════════════════
  Workflow 2: Terraform Template Pattern
  (With Error Handling & Retry Logic)
═══════════════════════════════════════════════
...

[Stage 3/5] Terraform Plan
─────────────────────────────
...
⚠ [TEST MODE] Simulating failure (attempt 1/1)

⚠ Stage failed (attempt 1/3): Planning failed: Simulated plan failure: Unable to read remote state

[Error Hook] Stage error detected:
  Stage: PLAN
  Attempt: 1/3
  Total errors so far: 1
  Retrying in 2 seconds...

[Stage 3/5] Terraform Plan
─────────────────────────────
...
✓ [TEST MODE] Allowing stage to succeed after 1 simulated failure(s)
✓ Plan generated successfully!
...
✓ Workflow completed successfully!
```

### Example: Testing Non-Recoverable Error

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="workflow2 -n 'Test' -t 'now' --simulate-failure VALIDATE"
```

**Expected Output:**

```
[Stage 2/5] Terraform Validate
─────────────────────────────
...
⚠ [TEST MODE] Simulating failure (attempt 1/999)

✗ Workflow failed at stage: VALIDATE
  Error: Validation failed: Simulated validation failure: Invalid resource reference
  Recoverable: false

[Cleanup Hook] Performing workflow cleanup...
  Failed stage: VALIDATE
  Total errors encountered: 1
  (Note: This was a simulated failure for testing)
  
✗ Workflow 2 failed!
```

---

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── workflow/
│   │               ├── WorkflowCliApplication.java
│   │               ├── MainCommand.java
│   │               ├── commands/
│   │               │   ├── Workflow1Command.java (uses Chain of Responsibility)
│   │               │   ├── Workflow2Command.java (uses Template Method)
│   │               │   └── Workflow3Command.java
│   │               ├── handlers/
│   │               │   ├── TerraformHandler.java (abstract base)
│   │               │   ├── WorkspaceNameHandler.java (uses Factory)
│   │               │   ├── TerraformInitHandler.java
│   │               │   ├── TerraformValidateHandler.java
│   │               │   ├── TerraformPlanHandler.java
│   │               │   ├── TerraformApplyHandler.java
│   │               │   └── TerraformOutputHandler.java
│   │               ├── factory/
│   │               │   ├── WorkspaceNameStrategy.java (strategy interface)
│   │               │   ├── WorkspaceNameFactory.java (factory class)
│   │               │   ├── EnvironmentBasedStrategy.java
│   │               │   ├── TimestampBasedStrategy.java
│   │               │   ├── CustomPrefixStrategy.java
│   │               │   └── SimpleStrategy.java
│   │               ├── template/
│   │               │   ├── exceptions/
│   │               │   │   ├── TerraformWorkflowException.java (base exception)
│   │               │   │   ├── InitializationException.java
│   │               │   │   ├── ValidationException.java
│   │               │   │   ├── PlanningException.java
│   │               │   │   ├── ApplyException.java
│   │               │   │   └── OutputException.java
│   │               │   ├── TerraformWorkflowTemplate.java (abstract template)
│   │               │   └── Workflow2TerraformTemplate.java (concrete impl)
│   │               └── context/
│   │                   └── TerraformContext.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
```

## Design Patterns

> 📊 **Visual diagrams available in [DIAGRAMS.md](DIAGRAMS.md)**

### Chain of Responsibility + Factory + Strategy Patterns (Workflow 1)

Workflow 1 demonstrates how multiple patterns work together with context passing and validation:

**Chain of Responsibility:**
- **TerraformHandler**: Abstract base class defining the chain interface
- **Concrete Handlers**: Six specialized handlers for each Terraform stage
- **Chain Flow**: Each handler processes its stage and passes control to the next
- **Error Handling**: If any stage fails, the chain stops and returns failure
- **Context Object**: `TerraformContext` is passed through the entire chain
- **Input Validation**: Handlers declare required attributes and validation occurs before execution

**Factory Pattern:**
- **WorkspaceNameFactory**: Creates different workspace naming strategies based on user input
- **Strategy Type Enum**: Defines available strategy types (ENVIRONMENT, TIMESTAMP, CUSTOM_PREFIX, SIMPLE)
- **Dynamic Creation**: Factory instantiates the appropriate strategy at runtime

**Strategy Pattern:**
- **WorkspaceNameStrategy**: Interface defining the strategy contract
- **Concrete Strategies**: Four different implementations for workspace naming
- **Runtime Selection**: Strategy is chosen at runtime based on command-line options

**Integration:**
The `WorkspaceNameHandler` (part of the chain) uses the Factory to create a Strategy, demonstrating how patterns compose together:

```java
// Factory creates strategy
WorkspaceNameStrategy strategy = WorkspaceNameFactory.createStrategy(strategyType, parameter);

// Strategy is used to generate workspace name
String workspaceName = strategy.generateWorkspaceName(workflowName, timeToRun);

// Handler passes to next in chain
return passToNext(workflowName, timeToRun);
```

**Context Passing:**
The `TerraformContext` object allows handlers to:
- Share state and data between stages
- Store intermediate results (e.g., workspace name, instance ID)
- Pass configuration throughout the pipeline
- Maintain workflow metadata

**Input Validation:**
Each handler can declare required attributes:
```java
@Override
protected Set<String> getRequiredAttributes() {
    return requireAttributes("workspace_name", "terraform_initialized");
}
```

Before executing, the framework validates that all required attributes exist in the context. If validation fails, execution stops with a clear error message.

Benefits:
- Decouples sender from receivers (Chain of Responsibility)
- Centralizes object creation logic (Factory)
- Allows runtime algorithm selection (Strategy)
- Shared state through context object
- Automatic input validation before handler execution
- Type-safe attribute access
- Clear error messages when validation fails
- Easy to add new naming strategies without modifying existing code
- Each handler has a single responsibility
- Flexible and extensible design

### Template Method Pattern (Workflow 2)

Workflow 2 implements the Template Method pattern to define the workflow algorithm structure with comprehensive error handling and context passing:

- **TerraformWorkflowTemplate**: Abstract class defining the template method (`executeWorkflow`)
- **Template Method**: Defines the invariant workflow skeleton (init → validate → plan → apply → output)
- **Context Passing**: `TerraformContext` is passed through all stages
- **Abstract Methods**: Each stage is an abstract method accepting context that must be implemented by subclasses
- **Stage Dependencies**: Each stage can verify prerequisites from context before execution
- **Hook Methods**: 
  - `printHeader()` and `printFooter()` - Customize output display
  - `onStageError()` - Called before each retry attempt
  - `cleanup()` - Called on final failure for rollback logic
- **Workflow2TerraformTemplate**: Concrete implementation with specific behavior for each stage

**Error Handling Architecture:**

1. **Custom Exceptions**
   - `TerraformWorkflowException` - Base exception with stage info
   - `InitializationException` - Init failures (recoverable)
   - `ValidationException` - Validation failures (non-recoverable)
   - `PlanningException` - Plan failures (recoverable)
   - `ApplyException` - Apply failures (non-recoverable)
   - `OutputException` - Output failures (recoverable)

2. **Retry Mechanism**
   ```java
   - Recoverable errors automatically retry
   - Exponential backoff (2s, 4s, 6s...)
   - Max retries configurable
   - onStageError() hook called before each retry
   ```

3. **Error Hooks**
   ```java
   protected void onStageError(TerraformWorkflowException e, int attempt)
   // Called before retry - implement custom recovery logic
   
   protected void cleanup(TerraformWorkflowException e)
   // Called on final failure - implement rollback logic
   ```

Benefits:
- Defines the algorithm structure in one place
- Prevents subclasses from changing the workflow sequence
- Promotes code reuse through inheritance
- Allows customization of specific steps while maintaining overall structure
- **Shared state through context object**
- **Stage prerequisite validation**
- **Built-in error handling with retry logic**
- **Extensible error recovery through hooks**
- **Automatic cleanup on failure**
- Easy to create new workflows by extending the template

**Context Passing:**
The `TerraformContext` object allows stages to:
- Share state and data between stages
- Store intermediate results (e.g., resource IDs, provider versions)
- Verify prerequisites before execution
- Maintain workflow metadata throughout execution

**Example:**
```java
// Init stage stores data in context
context.setAttribute("providers_initialized", true);
context.setAttribute("vpc_id", "vpc-abc123");

// Validate stage verifies prerequisite
Boolean providersInitialized = context.getAttribute("providers_initialized", Boolean.class);
if (providersInitialized == null || !providersInitialized) {
    return false; // Prerequisite not met
}

// Output stage retrieves resource IDs
String vpcId = context.getAttribute("vpc_id", String.class);
```

**Key Difference from Chain of Responsibility:**
- Template Method: The algorithm structure is fixed in the abstract class; subclasses fill in the details
- Chain of Responsibility: Handlers are independent and can be dynamically chained
- Template Method has built-in error handling and retry logic at the framework level
- Both patterns now support context passing for shared state

---

## Spring Boot Dependency Injection

This application leverages Spring Boot's powerful dependency injection capabilities to manage components and promote loose coupling.

### Benefits of Using Spring DI

1. **Testability**: Components can be easily mocked and tested in isolation
2. **Loose Coupling**: Dependencies are injected rather than created internally
3. **Lifecycle Management**: Spring manages bean creation and destruction
4. **Configuration**: Easy to swap implementations without changing code
5. **Maintainability**: Clear dependency relationships through constructor injection

### Spring-Managed Components

#### Handler Beans (Workflow 1)

All Terraform handlers are registered as Spring beans:

```java
@Component
public class TerraformInitHandler extends TerraformHandler { ... }

@Component
public class TerraformValidateHandler extends TerraformHandler { ... }

@Component
public class TerraformPlanHandler extends TerraformHandler { ... }

@Component
public class TerraformApplyHandler extends TerraformHandler { ... }

@Component
public class TerraformOutputHandler extends TerraformHandler { ... }
```

#### Template Bean (Workflow 2)

The workflow template is also a Spring-managed bean:

```java
@Component
public class Workflow2TerraformTemplate extends TerraformWorkflowTemplate { ... }
```

#### Factory Bean

For components that need runtime parameters, we use a factory bean:

```java
@Component
public class WorkspaceNameHandlerFactory {
    public WorkspaceNameHandler create(StrategyType type, String parameter) {
        return new WorkspaceNameHandler(type, parameter);
    }
}
```

### Constructor Injection

Both workflow commands use constructor injection to receive their dependencies:

**Workflow1Command:**
```java
@Component
public class Workflow1Command implements Runnable {
    private final WorkspaceNameHandlerFactory factory;
    private final TerraformInitHandler initHandler;
    private final TerraformValidateHandler validateHandler;
    private final TerraformPlanHandler planHandler;
    private final TerraformApplyHandler applyHandler;
    private final TerraformOutputHandler outputHandler;
    
    // Constructor injection (no @Autowired needed with single constructor)
    public Workflow1Command(WorkspaceNameHandlerFactory factory,
                           TerraformInitHandler initHandler,
                           TerraformValidateHandler validateHandler,
                           TerraformPlanHandler planHandler,
                           TerraformApplyHandler applyHandler,
                           TerraformOutputHandler outputHandler) {
        // Dependencies are injected by Spring
        this.factory = factory;
        this.initHandler = initHandler;
        // ... etc
    }
}
```

**Workflow2Command:**
```java
@Component
public class Workflow2Command implements Runnable {
    private final Workflow2TerraformTemplate template;
    
    public Workflow2Command(Workflow2TerraformTemplate template) {
        this.template = template;
    }
}
```

### Why Constructor Injection?

- **Immutability**: Final fields ensure dependencies don't change
- **Required Dependencies**: Constructor makes dependencies explicit and required
- **Testability**: Easy to create instances with mock dependencies in tests
- **Null Safety**: No risk of null dependencies if object construction succeeds

### Integration with Picocli

Spring Boot automatically integrates with picocli through the `picocli-spring-boot-starter` dependency, which:
- Registers all `@Command` classes as Spring beans
- Injects dependencies using Spring's IoC container
- Manages the lifecycle of command beans

This allows us to combine picocli's CLI capabilities with Spring's dependency injection seamlessly.

---

## License

This project is open source and available under the MIT License.

