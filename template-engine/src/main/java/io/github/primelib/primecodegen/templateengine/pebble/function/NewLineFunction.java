package io.github.primelib.primecodegen.templateengine.pebble.function;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewLineFunction implements Function {

    @Override
    public List<String> getArgumentNames() {
        List<String> names = new ArrayList<>();
        names.add("length");
        return names;
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        return "\r\n";
    }
}
