package io.github.primelib.primecodegen.templateengine.pebble.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WrapInFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        List<String> names = new ArrayList<>();
        names.add("left");
        names.add("right");
        return names;
    }

    @Override
    public Object apply(Object inputObj, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        String left = (String) args.get("left");
        String right = (String) args.get("right");

        if(inputObj == null) {
            return null;
        }

        return left + inputObj + right;
    }

}
