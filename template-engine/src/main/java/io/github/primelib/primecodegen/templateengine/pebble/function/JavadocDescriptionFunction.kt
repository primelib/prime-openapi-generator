package io.github.primelib.primecodegen.templateengine.pebble.function;

import io.pebbletemplates.pebble.extension.Function
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate
import org.apache.commons.lang3.StringUtils

class JavadocDescriptionFunction : Function {
    override fun getArgumentNames(): List<String> {
        return listOf("left", "summary")
    }

    override fun execute(
        args: MutableMap<String, Any>,
        self: PebbleTemplate,
        context: EvaluationContext,
        lineNumber: Int
    ): Any? {
        val left = args["left"] as String
        val summary = args["summary"] as? String

        if (StringUtils.isEmpty(summary)) {
            return ""
        }

        val lines = summary!!.split("\\s{2}".toRegex())
        return StringBuilder().apply {
            lines.map { line ->
                line.replace(Regex("""\`(.*?)\`""")) { matchResult ->
                        "{@code ${matchResult.groupValues[1]}}"
                    }
                    .replace(Regex("""\`\`\`(.*?)\`\`\`""")) { matchResult ->
                        "<pre>${matchResult.groupValues[1]}</pre>"
                    }
                    .replace("\\\"", "\"")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("&", "&amp;")
            }.forEach { line -> append("$left$line\n") }
        }.toString()
    }
}
