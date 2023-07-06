package io.github.primelib.primecodegen.templateengine.pebble.function;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetOrDefaultFunction implements Function {

    @Override
    public List<String> getArgumentNames() {
        List<String> names = new ArrayList<>();
        names.add("get");
        names.add("default");
        return names;
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        String left = (String) args.get("get");

        if (StringUtils.isEmpty(left)) {
            return args.get("default");
        }

        return left;
    }
}
