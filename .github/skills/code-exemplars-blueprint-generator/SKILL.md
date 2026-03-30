---
name: code-exemplars-blueprint-generator
description: 'Technology-agnostic prompt generator that creates customizable AI prompts for scanning codebases and identifying high-quality code exemplars. Supports multiple programming languages (.NET, Java, JavaScript, TypeScript, React, Angular, Python) with configurable analysis depth, categorization methods, and documentation formats to establish coding standards and maintain consistency across development teams.'
---

# Code Exemplars Blueprint Generator

## Configuration Variables
${PROJECT_TYPE="Auto-detect|.NET|Java|JavaScript|TypeScript|React|Angular|Python|Other"}
${SCAN_DEPTH="Basic|Standard|Comprehensive"}
${INCLUDE_CODE_SNIPPETS=true|false}
${CATEGORIZATION="Pattern Type|Architecture Layer|File Type"}
${MAX_EXAMPLES_PER_CATEGORY=3}
${INCLUDE_COMMENTS=true|false}

## Generated Prompt

"Scan this codebase and generate an exemplars.md file that identifies high-quality, representative code examples. The exemplars should demonstrate our coding standards and patterns to help maintain consistency. Use the following approach:

### 1. Codebase Analysis Phase
- Automatically detect primary programming languages and frameworks by scanning file extensions and configuration files
- Identify files with high-quality implementation, good documentation, and clear structure
- Look for commonly used patterns, architecture components, and well-structured implementations
- Prioritize files that demonstrate best practices for our technology stack
- Only reference actual files that exist in the codebase - no hypothetical examples

### 2. Exemplar Identification Criteria
- Well-structured, readable code with clear naming conventions
- Comprehensive comments and documentation
- Proper error handling and validation
- Adherence to design patterns and architectural principles
- Separation of concerns and single responsibility principle
- Efficient implementation without code smells
- Representative of our standard approaches

### 3. Core Pattern Categories

#### Java Exemplars (if detected)
- **Entity Classes**: Well-designed JPA entities or domain models
- **Service Implementations**: Clean service layer components
- **Repository Patterns**: Data access implementations
- **Controller/Resource Classes**: API endpoint implementations
- **Configuration Classes**: Application configuration
- **Unit Tests**: Well-structured JUnit tests

#### Frontend Exemplars (if detected)
- **Component Structure**: Clean, well-structured components
- **State Management**: Good examples of state handling
- **API Integration**: Well-implemented service calls and data handling
- **Form Handling**: Validation and submission patterns

### 4. Architecture Layer Exemplars

- **Presentation Layer**: Controllers/API endpoints, View models/DTOs
- **Business Logic Layer**: Service implementations, Workflow orchestration
- **Data Access Layer**: Repository implementations, Data models, Query patterns
- **Cross-Cutting Concerns**: Logging, Error handling, Auth, Validation

### 5. Exemplar Documentation Format

For each identified exemplar, document:
- File path (relative to repository root)
- Brief description of what makes it exemplary
- Pattern or component type it represents
- Key implementation details and coding principles demonstrated
- Small, representative code snippet (if applicable)

### 6. Output Format

Create exemplars.md with:
1. Introduction explaining the purpose of the document
2. Table of contents with links to categories
3. Organized sections based on selected categorization
4. Up to 3 exemplars per category
5. Conclusion with recommendations for maintaining code quality

The document should be actionable for developers needing guidance on implementing new features consistent with existing patterns.

Important: Only include actual files from the codebase. Verify all file paths exist. Do not include placeholder or hypothetical examples.
"

## Expected Output
Upon running this prompt, GitHub Copilot will scan your codebase and generate an exemplars.md file containing real references to high-quality code examples in your repository, organized according to your selected parameters.
