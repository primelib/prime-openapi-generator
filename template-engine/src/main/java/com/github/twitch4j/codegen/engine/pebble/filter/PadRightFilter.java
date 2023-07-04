package com.github.twitch4j.codegen.engine.pebble.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PadRightFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        List<String> names = new ArrayList<>();
        names.add("length");
        return names;
    }

    @Override
    public Object apply(Object inputObj, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        Long length = (Long) args.get("length");

        if(inputObj == null){
            return null;
        }

        String input = inputObj.toString();
        if (input.length() < length) {
            input = input + StringUtils.repeat(" ", (int) (length - input.length()));
            return input;
        }

        return inputObj;
    }

}
