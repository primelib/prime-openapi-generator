package io.github.primelib.primecodegen.templateengine.pebble.function;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.apache.commons.lang3.StringUtils;

class GetOrDefaultFunction : Function {
    override fun getArgumentNames(): List<String> {
        return listOf("get", "default")
    }

    override fun execute(
        args: MutableMap<String, Any>,
        self: PebbleTemplate,
        context: EvaluationContext,
        lineNumber: Int
    ): Any? {
        val get = args["get"] as? String

        if (StringUtils.isEmpty(get)) {
            return args["default"]
        }

        return get
    }
}
