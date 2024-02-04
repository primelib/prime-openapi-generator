package io.github.primelib.primecodegen.templateengine.pebble.function;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

class NewLineFunction : Function {
    override fun getArgumentNames(): List<String> {
        return listOf("length")
    }

    override fun execute(
        args: MutableMap<String, Any>,
        self: PebbleTemplate,
        context: EvaluationContext,
        lineNumber: Int
    ): Any {
        return "\n"
    }
}
