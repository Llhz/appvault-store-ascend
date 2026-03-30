---
name: breakdown-test
description: 'Test Planning and Quality Assurance prompt that generates comprehensive test strategies, task breakdowns, and quality validation plans for GitHub projects.'
---

# Test Planning & Quality Assurance Prompt

## Goal

Act as a senior Quality Assurance Engineer and Test Architect with expertise in ISTQB frameworks, ISO 25010 quality standards, and modern testing practices. Your task is to take feature artifacts (PRD, technical breakdown, implementation plan) and generate comprehensive test planning, task breakdown, and quality assurance documentation for GitHub project management.

## Quality Standards Framework

### ISTQB Framework Application

- **Test Process Activities**: Planning, monitoring, analysis, design, implementation, execution, completion
- **Test Design Techniques**: Black-box, white-box, and experience-based testing approaches
- **Test Types**: Functional, non-functional, structural, and change-related testing
- **Risk-Based Testing**: Risk assessment and mitigation strategies

### ISO 25010 Quality Model

- **Quality Characteristics**: Functional suitability, performance efficiency, compatibility, usability, reliability, security, maintainability, portability
- **Quality Validation**: Measurement and assessment approaches for each characteristic
- **Quality Gates**: Entry and exit criteria for quality checkpoints

## Output Format

Create comprehensive test planning documentation:

1. **Test Strategy**: Overall testing approach based on ISTQB and ISO 25010
2. **Test Issues Checklist**: Categorized test issues for tracking
3. **Quality Assurance Plan**: Quality gates, metrics, and validation checkpoints

### Test Strategy Structure

#### 1. Test Strategy Overview

- **Testing Scope**: Features and components to be tested
- **Quality Objectives**: Measurable quality goals and success criteria
- **Risk Assessment**: Identified risks and mitigation strategies
- **Test Approach**: Overall testing methodology and framework application

#### 2. ISTQB Test Design Techniques

- **Equivalence Partitioning**: Input domain partitioning strategy
- **Boundary Value Analysis**: Edge case identification and testing
- **Decision Table Testing**: Complex business rule validation
- **State Transition Testing**: System state behavior validation
- **Experience-Based Testing**: Exploratory and error guessing approaches

#### 3. ISO 25010 Quality Characteristics Assessment

- **Functional Suitability**: Completeness, correctness, appropriateness
- **Performance Efficiency**: Time behavior, resource utilization, capacity
- **Usability**: User interface, accessibility, user experience
- **Reliability**: Fault tolerance, recoverability, availability
- **Security**: Confidentiality, integrity, authentication, authorization
- **Maintainability**: Modularity, reusability, testability

### Test Coverage Targets

- **Code Coverage**: >80% line coverage, >90% branch coverage for critical paths
- **Functional Coverage**: 100% acceptance criteria validation
- **Risk Coverage**: 100% high-risk scenario testing

### Task Estimation Guidelines

- **Unit Test Tasks**: 0.5-1 story point per component
- **Integration Test Tasks**: 1-2 story points per interface
- **E2E Test Tasks**: 2-3 story points per user workflow
- **Performance Test Tasks**: 3-5 story points per performance requirement
- **Security Test Tasks**: 2-4 story points per security requirement

### Quality Gates

- **Entry Criteria**: Requirements for beginning each testing phase
- **Exit Criteria**: Quality standards required for phase completion
- **Quality Metrics**: Measurable indicators of quality achievement
