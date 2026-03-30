---
name: architecture-blueprint-generator
description: 'Comprehensive project architecture blueprint generator that analyzes codebases to create detailed architectural documentation. Automatically detects technology stacks and architectural patterns, generates visual diagrams, documents implementation patterns, and provides extensible blueprints for maintaining architectural consistency and guiding new development.'
---

# Comprehensive Project Architecture Blueprint Generator

## Configuration Variables
${PROJECT_TYPE="Auto-detect|.NET|Java|React|Angular|Python|Node.js|Flutter|Other|}
${ARCHITECTURE_PATTERN="Auto-detect|Clean Architecture|Microservices|Layered|MVVM|MVC|Hexagonal|Event-Driven|Serverless|Monolithic|Other"}
${DIAGRAM_TYPE="C4|UML|Flow|Component|None"}
${DETAIL_LEVEL="High-level|Detailed|Comprehensive|Implementation-Ready"}
${INCLUDES_CODE_EXAMPLES=true|false}
${INCLUDES_IMPLEMENTATION_PATTERNS=true|false}
${INCLUDES_DECISION_RECORDS=true|false}
${FOCUS_ON_EXTENSIBILITY=true|false}

## Generated Prompt

"Create a comprehensive 'Project_Architecture_Blueprint.md' document that thoroughly analyzes the architectural patterns in the codebase to serve as a definitive reference for maintaining architectural consistency. Use the following approach:

### 1. Architecture Detection and Analysis
- Analyze the project structure to identify all technology stacks and frameworks in use by examining:
  - Project and configuration files
  - Package dependencies and import statements
  - Framework-specific patterns and conventions
  - Build and deployment configurations

- Determine the architectural pattern(s) by analyzing:
  - Folder organization and namespacing
  - Dependency flow and component boundaries
  - Interface segregation and abstraction patterns
  - Communication mechanisms between components

### 2. Architectural Overview
- Provide a clear, concise explanation of the overall architectural approach
- Document the guiding principles evident in the architectural choices
- Identify architectural boundaries and how they're enforced
- Note any hybrid architectural patterns or adaptations of standard patterns

### 3. Architecture Visualization
Create diagrams at multiple levels of abstraction:
- High-level architectural overview showing major subsystems
- Component interaction diagrams showing relationships and dependencies
- Data flow diagrams showing how information moves through the system
- Ensure diagrams accurately reflect the actual implementation, not theoretical patterns

### 4. Core Architectural Components
For each architectural component discovered in the codebase:

- **Purpose and Responsibility**: Primary function, business domains addressed, scope limitations
- **Internal Structure**: Organization of classes/modules, key abstractions, design patterns
- **Interaction Patterns**: Communication mechanisms, interfaces, dependency injection, events
- **Evolution Patterns**: Extension points, plugin mechanisms, configuration approaches

### 5. Architectural Layers and Dependencies
- Map the layer structure as implemented in the codebase
- Document the dependency rules between layers
- Identify abstraction mechanisms that enable layer separation
- Note any circular dependencies or layer violations

### 6. Data Architecture
- Document domain model structure and organization
- Map entity relationships and aggregation patterns
- Identify data access patterns (repositories, data mappers, etc.)
- Document data transformation and mapping approaches

### 7. Cross-Cutting Concerns Implementation
- **Authentication & Authorization**: Security model, permissions, identity management
- **Error Handling & Resilience**: Exception handling, retry, circuit breaker, fallback
- **Logging & Monitoring**: Instrumentation, observability, diagnostics
- **Validation**: Input validation, business rules, error reporting
- **Configuration Management**: Configuration sources, environment-specific, secrets

### 8. Testing Architecture
- Document testing strategies aligned with the architecture
- Identify test boundary patterns (unit, integration, system)
- Map test doubles and mocking approaches

### 9. Blueprint for New Development
- Development workflow and starting points for different feature types
- Component creation sequence and integration steps
- Common pitfalls and architecture violations to avoid
"
