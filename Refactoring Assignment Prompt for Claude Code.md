# **Refactoring Assignment Prompt for Claude Code**

*(Markdown format — copy and paste directly)*

------

# **Task Overview**

You are assisting in a university software engineering assignment.
 The goal is to **refactor an existing software project using design patterns**, and produce:

1. Refactored source code (with annotations)
2. UML diagrams
3. A detailed English report
4. Presentation slides (content only; no need for real PDF output)

You MUST strictly follow the instructions below.

------

# Project to Be Refactored

**[Replace this with the link / folder description of my project]**

------

# Refactoring Requirements

You MUST apply the following SIX design patterns:

### **One Creational Pattern** (choose the most suitable)

- Factory Method / Abstract Factory
- Builder
- Prototype
- Singleton（only if appropriate）

### **Two Structural Patterns**

Example options:

- Adapter
- Decorator
- Proxy
- Facade
- Composite
- Bridge
- Flyweight

### **Two Behavioral Patterns**

Example options:

- Strategy
- Observer
- State
- Command
- Template Method
- Chain of Responsibility

### **One Additional Pattern NOT Covered in Lectures**

(Must come from Wikipedia’s design pattern list)
 Suggested options:

- Mediator
- Visitor
- Specification
- Dependency Injection
- Repository Pattern
- Event Aggregator

You must justify each chosen pattern.

------

# General Rules for Refactoring

You MUST:

### Maintain original functionality (behavior must not change)

### Focus on improving:

- Modularity
- Extensibility
- Reusability
- Readability
- Reduction of code smells

### Annotate every modified part with:

```
// Refactored with XXX Pattern
```

or

```
# Refactored with XXX Pattern
```

### Produce BEFORE and AFTER code comparisons

### Generate UML Class Diagrams:

- Before refactor
- After refactor
- Highlighting the effect of each pattern

UML may be generated as PlantUML or Mermaid.

------

# Report Requirements (Write in English)

Generate a full-length report in Markdown including:

------

## **1. Project Information**

- Project name
- Team number (placeholder)
- Member names (placeholder)
- Student IDs (placeholder)
- Contact information (placeholder)

------

## **2. Project Description**

Include:

- Background
- Purpose
- Major functionalities
- Technologies used

------

## **3. Refactoring Details (MOST IMPORTANT SECTION)**

For each design pattern, include:

### **3.1 Problem Analysis (Before Refactoring)**

Identify:

- Code smells
- Design issues
- Maintainability problems
- Coupling & cohesion issues

### **3.2 Refactoring Solution (Pattern Applied)**

For each of the SIX patterns:

1. Name of pattern
2. Why it was chosen
3. Before → After UML diagrams
4. Before → After code snippets
5. Changes explained in detail
6. Benefits gained
7. How AI assisted in this transformation

This section MUST be extremely detailed.

------

## **4. AI Usage During Refactoring**

You MUST generate a reflective section describing:

### How AI was used for:

- Identifying refactoring opportunities
- Detecting code smells
- Selecting appropriate patterns
- Proposing class designs
- Generating code
- Evaluating the refactoring quality

### Challenges & Limitations of AI

- Wrong pattern suggestions
- Incorrect code
- Over-generalized designs
- Need for manual verification

### Best Practices Learned

Example:

- "Prompting AI with the entire module produces better architectural suggestions."
- "Incremental prompting improves correctness."

------

## **5. Additional Discussions**

Include:

- Unsolved issues
- Future improvements
- Testing considerations
- Team collaboration notes

------

# Final Deliverables Claude Must Produce

Claude must output EVERYTHING below:

### 1. **Refactored Source Code**

With clear comments:

```
// Refactored with Strategy Pattern
```

Only changed files need to be output if project is large.

------

### 2. **UML diagrams**

For each pattern:

- Original UML
- Refactored UML

Use **Mermaid** or **PlantUML**.

------

### 3. **English Project Report (Markdown)**

Full document following the structure above.

------

### 4. **Presentation Slide Content (Markdown or TXT)**

10-minute presentation containing:

1. Project intro
2. Why refactoring was needed
3. Six patterns explained (with before/after diagrams)
4. AI usage explanation
5. Conclusion

------

# **Additional Instructions**

- Keep everything in **clear, academic, formal English**.
- Keep explanations self-contained so they can be directly converted into PDF.
- Ensure code examples are correct and compilable.

------

# **End of Prompt**