package io.github.primelib.primecodegen.templateengine.pebble.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

class LinePrefixFilter : Filter {
    override fun getArgumentNames(): List<String> {
        return listOf("prefix")
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

        val prefix = args["prefix"] as? String
        val lines = inputObj.toString().lines()
        return buildString {
            lines.forEach { line ->
                append("$prefix$line\r\n")
            }
        }
    }
}
