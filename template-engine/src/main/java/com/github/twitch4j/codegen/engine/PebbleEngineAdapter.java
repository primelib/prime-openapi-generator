package com.github.twitch4j.codegen.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.twitch4j.codegen.engine.pebble.CodeGenPebbleExtension;
import com.github.twitch4j.codegen.engine.pebble.CodeGeneratorTemplateExecutorLoader;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.api.TemplatingEngineAdapter;
import org.openapitools.codegen.api.TemplatingExecutor;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

@Slf4j
public class PebbleEngineAdapter implements TemplatingEngineAdapter {

    @Getter
    private final String identifier = "peb";

    @Getter
    private final String[] fileExtensions = new String[]{"peb", "pebble"};

    private CodeGeneratorTemplateExecutorLoader loader;

    private PebbleEngine engine;

    public PebbleEngineAdapter() {
        loader = new CodeGeneratorTemplateExecutorLoader();
        engine = new PebbleEngine.Builder()
                .cacheActive(false)
                .newLineTrimming(true)
                .extension(new CodeGenPebbleExtension())
                .autoEscaping(false)
                .loader(new DelegatingLoader(List.of(new ClasspathLoader(), loader)))
                .build();
    }

    @Override
    public String compileTemplate(TemplatingExecutor executor, Map<String, Object> bundle, String templateFile) throws IOException {
        loader.setTemplatingExecutor(executor);

        log.warn("Processing Pebble Template: {}", templateFile);
        if (log.isTraceEnabled()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            log.warn("Bundle Data: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bundle));
        }

        // render
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate = engine.getTemplate(templateFile);
        compiledTemplate.evaluate(writer, bundle);
        return writer.toString();
    }
}
