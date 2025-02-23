package io.github.primelib.primecodegen.templateengine.pebble.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.apache.commons.lang3.StringUtils

@Suppress("ReturnCount")
class StringFormatFilter : Filter {
    override fun getArgumentNames(): List<String> {
        return listOf("template")
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

        val template = args["template"] as String
        if (StringUtils.isEmpty(template)) {
            return inputObj
        }

        return String.format(template, inputObj)
    }
}
