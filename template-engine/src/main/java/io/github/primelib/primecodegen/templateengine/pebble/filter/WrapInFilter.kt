package io.github.primelib.primecodegen.templateengine.pebble.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

class WrapInFilter : Filter {
    override fun getArgumentNames(): List<String> {
        return listOf("left", "right")
    }

    override fun apply(
        inputObj: Any?,
        args: MutableMap<String, Any>,
        self: PebbleTemplate,
        context: EvaluationContext,
        lineNumber: Int
    ): Any? {
        if (inputObj == null) {
            return null
        }

        val left = args["left"] as? String
        val right = args["right"] as? String
        return "$left$inputObj$right"
    }
}
