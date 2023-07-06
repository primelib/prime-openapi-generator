package io.github.primelib.primecodegen.templateengine.pebble;

import io.pebbletemplates.pebble.loader.ClasspathLoader;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.api.TemplatingExecutor;

import java.io.Reader;
import java.io.StringReader;

/**
 * A simple loader that will use the openapi-codegen templatingExecutor if available
 */
@Slf4j
public class CodeGeneratorTemplateExecutorLoader extends ClasspathLoader {

    @Setter
    private TemplatingExecutor templatingExecutor;

    @Override
    public Reader getReader(String templateName) {
        if (templatingExecutor != null) {
            String content = templatingExecutor.getFullTemplateContents(templateName);
            return new StringReader(content);
        }

        return null;
    }

}
