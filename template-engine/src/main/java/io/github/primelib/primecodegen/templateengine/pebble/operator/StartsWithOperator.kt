package io.github.primelib.primecodegen.templateengine.pebble.operator;

import io.pebbletemplates.pebble.node.expression.BinaryExpression;
import io.pebbletemplates.pebble.operator.Associativity;
import io.pebbletemplates.pebble.operator.BinaryOperator;
import io.pebbletemplates.pebble.operator.BinaryOperatorType;

class StartsWithOperator : BinaryOperator {

    /**
     * This precedence is set based on
     * [Java Operators](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html).
     * 30 is the same precedence pebble has set for operators like `instanceof` like
     * [Extending Pebble](https://github.com/PebbleTemplates/pebble/wiki/extending-pebble).
     */
    override fun getPrecedence(): Int {
        return 30
    }

    override fun getSymbol(): String {
        return "startswith"
    }

    override fun getType(): BinaryOperatorType {
        return BinaryOperatorType.NORMAL
    }

    override fun createInstance(): BinaryExpression<*> {
        return StartsWithExpression()
    }

    override fun getAssociativity(): Associativity {
        return Associativity.LEFT
    }
}
