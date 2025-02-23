package io.github.primelib.primecodegen.templateengine.pebble;

import io.pebbletemplates.pebble.loader.ClasspathLoader;
import org.openapitools.codegen.api.TemplatingExecutor;
import java.io.File

import java.io.Reader;
import java.io.StringReader;

/**
 * A simple loader that will use the openapi-codegen templatingExecutor if available
 */
@Suppress("ReturnCount")
class CodeGeneratorTemplateExecutorLoader : ClasspathLoader() {
    var templatingExecutor: TemplatingExecutor? = null

    override fun getReader(templateName: String): Reader? {
        // override templates
        val reader = getReaderOverride(templateName)
        if (reader != null) {
            return reader
        }

        // embedded templates
        if (templatingExecutor != null) {
            val content = templatingExecutor!!.getFullTemplateContents(templateName)
            return StringReader(content)
        }

        return null
    }

    /**
     * looks for template overwrites in PRIMECODEGEN_TEMPLATE_OVERRIDE_DIR if available
     */
    private fun getReaderOverride(templateName: String): Reader? {
        val templateDir = System.getenv("PRIMECODEGEN_TEMPLATE_OVERRIDE_DIR") ?: return null
        val overrideFileFile = File(templateDir, templateName)

        if (overrideFileFile.exists()) {
            return overrideFileFile.reader()
        }

        return null
    }
}
