package io.github.primelib.primecodegen.templateengine;

import mu.KotlinLogging
import org.apache.commons.io.FilenameUtils;
import org.openapitools.codegen.api.TemplatingEngineAdapter;
import org.openapitools.codegen.api.TemplatingExecutor;
import org.openapitools.codegen.templating.HandlebarsEngineAdapter;
import org.openapitools.codegen.templating.MustacheEngineAdapter;

private val logger = KotlinLogging.logger {}

class AutoEngineAdapter : TemplatingEngineAdapter {
    companion object {
        private val engineAdapters = listOf(
            MustacheEngineAdapter(),
            HandlebarsEngineAdapter(),
            PebbleEngineAdapter()
        )
        private val extensions: List<String> = engineAdapters
            .flatMap { it.fileExtensions.toList() }
            .toSet()
            .toList()

        fun getSupportedExtensions(): List<String> {
            return extensions
        }
    }

    override fun getIdentifier(): String {
        return "auto"
    }

    override fun getFileExtensions(): Array<String> {
        return extensions.toTypedArray()
    }

    override fun handlesFile(filename: String?): Boolean {
        if (filename == null) {
            return false
        }

        val extension = FilenameUtils.getExtension(filename)
        return engineAdapters.any { a -> a.fileExtensions.any { e -> e.equals(extension, ignoreCase = true) } }
    }

    override fun compileTemplate(
        executor: TemplatingExecutor,
        bundle: Map<String, Any>,
        templateFile: String
    ): String? {
        val extension = FilenameUtils.getExtension(templateFile)

        // select the correct engine adapter for this file extension
        for (engineAdapter in engineAdapters) {
            if (engineAdapter.fileExtensions.any { it.equals(extension, ignoreCase = true) }) {
                logger.debug("Template {} will use engine {}", templateFile, engineAdapter.identifier)
                return engineAdapter.compileTemplate(executor, bundle, templateFile)
            }
        }

        logger.debug("No template engine found for file extension {} - File: {}", extension, templateFile)
        return null
    }
}
