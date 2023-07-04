package com.github.twitch4j.codegen.engine.pebble.operator;

import io.pebbletemplates.pebble.node.expression.BinaryExpression;
import io.pebbletemplates.pebble.operator.Associativity;
import io.pebbletemplates.pebble.operator.BinaryOperator;
import io.pebbletemplates.pebble.operator.BinaryOperatorType;

public class StartsWithOperator implements BinaryOperator {

    public BinaryExpression<?> createInstance() {
        return new StartsWithExpression();
    }

    /**
     * This precedence is set based on
     * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html">Java
     * Operators</a> 30 is the same precedence pebble has set for operators like {@code instanceof}
     * like <a href="https://github.com/PebbleTemplates/pebble/wiki/extending-pebble">Extending
     * Pebble</a>.
     */
    public int getPrecedence() {
        return 30;
    }

    public String getSymbol() {
        return "startswith";
    }

    public BinaryOperatorType getType() {
        return BinaryOperatorType.NORMAL;
    }

    public BinaryExpression<?> getInstance() {
        return new StartsWithExpression();
    }

    public Associativity getAssociativity() {
        return Associativity.LEFT;
    }

}
