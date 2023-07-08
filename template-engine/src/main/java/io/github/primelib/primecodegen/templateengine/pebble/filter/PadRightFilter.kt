package io.github.primelib.primecodegen.templateengine.pebble.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.apache.commons.lang3.StringUtils;

class PadRightFilter : Filter {
    override fun getArgumentNames(): List<String> {
        return listOf("length")
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

        val length = args["length"] as Long
        val input = inputObj.toString()
        return if (input.length < length) {
            input + StringUtils.repeat(" ", (length - input.length).toInt())
        } else {
            inputObj
        }
    }
}
