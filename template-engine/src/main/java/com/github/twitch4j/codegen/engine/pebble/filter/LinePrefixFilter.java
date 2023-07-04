package com.github.twitch4j.codegen.engine.pebble.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LinePrefixFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        List<String> names = new ArrayList<>();
        names.add("prefix");
        return names;
    }

    @Override
    public Object apply(Object inputObj, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        String prefix = (String) args.get("prefix");

        if(inputObj == null) {
            return null;
        }

        StringBuffer output = new StringBuffer();
        Arrays.asList(inputObj.toString().split("\n")).forEach(line -> output.append(prefix + line + "\r\n"));
        return output.toString();
    }

}
