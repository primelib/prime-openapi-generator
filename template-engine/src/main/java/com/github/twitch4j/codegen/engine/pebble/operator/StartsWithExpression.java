package com.github.twitch4j.codegen.engine.pebble.operator;

import io.pebbletemplates.pebble.node.expression.BinaryExpression;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

public class StartsWithExpression extends BinaryExpression<Object> {

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
        // left & right value
        String left = getLeftExpression().evaluate(self, context).toString();
        String right = getRightExpression().evaluate(self, context).toString();

        // Check if the left class is an instanceof the right class
        return left.startsWith(right);
    }

}
