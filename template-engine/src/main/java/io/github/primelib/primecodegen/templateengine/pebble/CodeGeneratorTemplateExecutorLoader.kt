package io.github.primelib.primecodegen.templateengine.pebble;

import io.pebbletemplates.pebble.loader.ClasspathLoader;
import org.openapitools.codegen.api.TemplatingExecutor;

import java.io.Reader;
import java.io.StringReader;

/**
 * A simple loader that will use the openapi-codegen templatingExecutor if available
 */
class CodeGeneratorTemplateExecutorLoader : ClasspathLoader() {
    var templatingExecutor: TemplatingExecutor? = null

    override fun getReader(templateName: String): Reader? {
        if (templatingExecutor != null) {
            val content = templatingExecutor!!.getFullTemplateContents(templateName)
            return StringReader(content)
        }

        return null
    }
}
