package carrotfantasy;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

// Refactored with Interpreter Pattern
// Abstract expression interface
interface GameExpression {
    boolean interpret(GameContext context);
}

// Terminal expressions - represent primitive operations
class VariableExpression implements GameExpression {
    private String variableName;

    public VariableExpression(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public boolean interpret(GameContext context) {
        Object value = context.getVariable(variableName);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        return value != null;
    }
}

class NumberExpression implements GameExpression {
    private double value;

    public NumberExpression(double value) {
        this.value = value;
    }

    @Override
    public boolean interpret(GameContext context) {
        return value != 0;
    }

    public double getValue() {
        return value;
    }
}

// Non-terminal expressions - represent composite operations
class AndExpression implements GameExpression {
    private GameExpression left;
    private GameExpression right;

    public AndExpression(GameExpression left, GameExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(GameContext context) {
        return left.interpret(context) && right.interpret(context);
    }
}

class OrExpression implements GameExpression {
    private GameExpression left;
    private GameExpression right;

    public OrExpression(GameExpression left, GameExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(GameContext context) {
        return left.interpret(context) || right.interpret(context);
    }
}

class NotExpression implements GameExpression {
    private GameExpression expression;

    public NotExpression(GameExpression expression) {
        this.expression = expression;
    }

    @Override
    public boolean interpret(GameContext context) {
        return !expression.interpret(context);
    }
}

// Comparison expressions
class GreaterThanExpression implements GameExpression {
    private GameExpression left;
    private GameExpression right;

    public GreaterThanExpression(GameExpression left, GameExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(GameContext context) {
        double leftVal = getNumericValue(left, context);
        double rightVal = getNumericValue(right, context);
        return leftVal > rightVal;
    }

    private double getNumericValue(GameExpression expr, GameContext context) {
        if (expr instanceof NumberExpression) {
            return ((NumberExpression) expr).getValue();
        }
        if (expr instanceof VariableExpression) {
            Object value = context.getVariable(((VariableExpression) expr).getVariableName());
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }
        return 0;
    }
}

class LessThanExpression implements GameExpression {
    private GameExpression left;
    private GameExpression right;

    public LessThanExpression(GameExpression left, GameExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(GameContext context) {
        double leftVal = getNumericValue(left, context);
        double rightVal = getNumericValue(right, context);
        return leftVal < rightVal;
    }

    private double getNumericValue(GameExpression expr, GameContext context) {
        if (expr instanceof NumberExpression) {
            return ((NumberExpression) expr).getValue();
        }
        if (expr instanceof VariableExpression) {
            Object value = context.getVariable(((VariableExpression) expr).getVariableName());
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }
        return 0;
    }
}

class GreaterThanOrEqualExpression implements GameExpression {
    private GameExpression left;
    private GameExpression right;

    public GreaterThanOrEqualExpression(GameExpression left, GameExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(GameContext context) {
        double leftVal = getNumericValue(left, context);
        double rightVal = getNumericValue(right, context);
        return leftVal >= rightVal;
    }

    private double getNumericValue(GameExpression expr, GameContext context) {
        if (expr instanceof NumberExpression) {
            return ((NumberExpression) expr).getValue();
        }
        if (expr instanceof VariableExpression) {
            Object value = context.getVariable(((VariableExpression) expr).getVariableName());
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }
        return 0;
    }
}

class LessThanOrEqualExpression implements GameExpression {
    private GameExpression left;
    private GameExpression right;

    public LessThanOrEqualExpression(GameExpression left, GameExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(GameContext context) {
        double leftVal = getNumericValue(left, context);
        double rightVal = getNumericValue(right, context);
        return leftVal <= rightVal;
    }

    private double getNumericValue(GameExpression expr, GameContext context) {
        if (expr instanceof NumberExpression) {
            return ((NumberExpression) expr).getValue();
        }
        if (expr instanceof VariableExpression) {
            Object value = context.getVariable(((VariableExpression) expr).getVariableName());
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }
        return 0;
    }
}

class EqualsExpression implements GameExpression {
    private GameExpression left;
    private GameExpression right;

    public EqualsExpression(GameExpression left, GameExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(GameContext context) {
        Object leftVal = getValue(left, context);
        Object rightVal = getValue(right, context);
        if (leftVal == null && rightVal == null) return true;
        if (leftVal == null || rightVal == null) return false;
        return leftVal.equals(rightVal);
    }

    private Object getValue(GameExpression expr, GameContext context) {
        if (expr instanceof NumberExpression) {
            return ((NumberExpression) expr).getValue();
        }
        if (expr instanceof VariableExpression) {
            return context.getVariable(((VariableExpression) expr).getVariableName());
        }
        return null;
    }
}

// Context - stores variable values
class GameContext {
    private Map<String, Object> variables;

    public GameContext() {
        this.variables = new HashMap<>();
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public void clear() {
        variables.clear();
    }
}

// Parser - builds expression tree from string
class GameRuleParser {
    public static GameExpression parse(String expression) {
        // Simple recursive descent parser
        // Supports: AND, OR, NOT, >, <, ==, variables, numbers
        expression = expression.trim();
        
        // Handle parentheses
        if (expression.startsWith("(") && expression.endsWith(")")) {
            expression = expression.substring(1, expression.length() - 1);
        }

        // Parse OR (lowest precedence)
        int orIndex = findOperator(expression, " OR ", "||");
        if (orIndex != -1) {
            GameExpression left = parse(expression.substring(0, orIndex));
            GameExpression right = parse(expression.substring(orIndex + 4));
            return new OrExpression(left, right);
        }

        // Parse AND
        int andIndex = findOperator(expression, " AND ", "&&");
        if (andIndex != -1) {
            GameExpression left = parse(expression.substring(0, andIndex));
            GameExpression right = parse(expression.substring(andIndex + 5));
            return new AndExpression(left, right);
        }

        // Parse NOT
        if (expression.startsWith("NOT ") || expression.startsWith("!")) {
            String subExpr = expression.startsWith("NOT ") ? 
                expression.substring(4) : expression.substring(1);
            return new NotExpression(parse(subExpr));
        }

        // Parse comparisons
        if (expression.contains(" >= ")) {
            int index = expression.indexOf(" >= ");
            return new GreaterThanOrEqualExpression(
                parse(expression.substring(0, index)),
                parse(expression.substring(index + 4))
            );
        }
        if (expression.contains(" <= ")) {
            int index = expression.indexOf(" <= ");
            return new LessThanOrEqualExpression(
                parse(expression.substring(0, index)),
                parse(expression.substring(index + 4))
            );
        }
        if (expression.contains(" > ")) {
            int index = expression.indexOf(" > ");
            return new GreaterThanExpression(
                parse(expression.substring(0, index)),
                parse(expression.substring(index + 3))
            );
        }
        if (expression.contains(" < ")) {
            int index = expression.indexOf(" < ");
            return new LessThanExpression(
                parse(expression.substring(0, index)),
                parse(expression.substring(index + 3))
            );
        }
        if (expression.contains(" == ") || expression.contains(" = ")) {
            int index = expression.contains(" == ") ? 
                expression.indexOf(" == ") : expression.indexOf(" = ");
            String op = expression.contains(" == ") ? " == " : " = ";
            return new EqualsExpression(
                parse(expression.substring(0, index)),
                parse(expression.substring(index + op.length()))
            );
        }

        // Parse variable or number
        expression = expression.trim();
        try {
            double num = Double.parseDouble(expression);
            return new NumberExpression(num);
        } catch (NumberFormatException e) {
            return new VariableExpression(expression);
        }
    }

    private static int findOperator(String expr, String op1, String op2) {
        int depth = 0;
        for (int i = 0; i < expr.length(); i++) {
            if (expr.charAt(i) == '(') depth++;
            else if (expr.charAt(i) == ')') depth--;
            else if (depth == 0) {
                if (i + op1.length() <= expr.length() && 
                    expr.substring(i, i + op1.length()).equals(op1)) {
                    return i;
                }
                if (i + op2.length() <= expr.length() && 
                    expr.substring(i, i + op2.length()).equals(op2)) {
                    return i;
                }
            }
        }
        return -1;
    }
}

// Interpreter facade - provides easy-to-use interface
class GameRuleInterpreter {
    private GameExpression expression;
    private GameContext context;

    public GameRuleInterpreter(String ruleExpression) {
        this.expression = GameRuleParser.parse(ruleExpression);
        this.context = new GameContext();
    }

    public void setVariable(String name, Object value) {
        context.setVariable(name, value);
    }

    public boolean evaluate() {
        return expression.interpret(context);
    }

    public void clearContext() {
        context.clear();
    }
}

