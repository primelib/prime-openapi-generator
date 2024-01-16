package io.github.primelib.primecodegen.templateengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.primelib.primecodegen.templateengine.pebble.CodeGenPebbleExtension;
import io.github.primelib.primecodegen.templateengine.pebble.CodeGeneratorTemplateExecutorLoader;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.DelegatingLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import mu.KotlinLogging
import org.openapitools.codegen.api.TemplatingEngineAdapter;
import org.openapitools.codegen.api.TemplatingExecutor;
import java.io.StringWriter

private val logger = KotlinLogging.logger {}

class PebbleEngineAdapter : TemplatingEngineAdapter {
    override fun getIdentifier(): String {
        return "peb"
    }

    override fun getFileExtensions(): Array<String> {
        return arrayOf("peb", "pebble")
    }

    private val loader: CodeGeneratorTemplateExecutorLoader = CodeGeneratorTemplateExecutorLoader()
    private val engine: PebbleEngine = PebbleEngine.Builder()
        .cacheActive(false)
        .newLineTrimming(true)
        .extension(CodeGenPebbleExtension())
        .autoEscaping(false)
        .loader(DelegatingLoader(listOf(ClasspathLoader(), loader)))
        .build()

    override fun compileTemplate(
        executor: TemplatingExecutor,
        bundle: Map<String, Any>,
        templateFile: String
    ): String {
        loader.templatingExecutor = executor

        logger.debug("Processing Pebble Template: {}", templateFile)
        if (logger.isTraceEnabled) {
            val mapper = ObjectMapper()
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            logger.debug("Bundle Data: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bundle))
        }

        // render
        val writer = StringWriter()
        val compiledTemplate: PebbleTemplate = engine.getTemplate(templateFile)
        compiledTemplate.evaluate(writer, bundle)
        return writer.toString().replace("\r\n", "\n")
    }
}
