package io.github.primelib.primecodegen.templateengine.pebble.operator;

import io.pebbletemplates.pebble.node.expression.BinaryExpression;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

class StartsWithExpression : BinaryExpression<Any>() {
    override fun evaluate(self: PebbleTemplateImpl, context: EvaluationContextImpl): Any {
        // left & right value
        val left = getLeftExpression().evaluate(self, context).toString()
        val right = getRightExpression().evaluate(self, context).toString()

        // Check if the left value starts with the right value
        return left.startsWith(right)
    }
}
