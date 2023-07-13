package io.github.primelib.primecodegen.templateengine.pebble.function;

import io.pebbletemplates.pebble.extension.Function
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate
import org.apache.commons.lang3.StringUtils

class JavadocDescriptionInlineFunction : Function {
    override fun getArgumentNames(): List<String> {
        return listOf("summary")
    }

    override fun execute(
        args: MutableMap<String, Any>,
        self: PebbleTemplate,
        context: EvaluationContext,
        lineNumber: Int
    ): Any? {
        val summary = args["summary"] as? String
        if (StringUtils.isEmpty(summary)) {
            return ""
        }

        return summary!!.replace("```", "\"")
                        .replace("\\\"", "\"")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("&", "&amp;")
    }
}
