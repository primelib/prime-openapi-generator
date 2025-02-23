package io.github.primelib.primecodegen.core.generator

import io.github.primelib.primecodegen.core.api.PrimeCodegenBase
import io.github.primelib.primecodegen.core.extensions.preprocessOperations
import io.github.primelib.primecodegen.core.postprocess.JavaImportPostProcessor
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import org.openapitools.codegen.CodegenConfig
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenProperty
import org.openapitools.codegen.languages.AbstractGoCodegen
import org.openapitools.codegen.languages.AbstractJavaCodegen
import org.openapitools.codegen.model.ModelMap
import org.openapitools.codegen.model.OperationsMap
import java.io.File
import java.io.IOException

/**
 * ExtendableJavaCodegen
 * <p>
 * This is an extendable version of the AbstractJavaCodegen, which has all generated templates and legacy imports removed.
 */
@Suppress("MaxLineLength")
abstract class ExtendableGoCodegenBase : AbstractGoCodegen(), CodegenConfig, PrimeCodegenBase {
    private val containerInnerTypePattern = Regex("""^.*<(.+)>$""")

    init {
        modelTemplateFiles.clear()
        apiTemplateFiles.clear()
        apiTestTemplateFiles.clear()
        modelDocTemplateFiles.clear()
        apiDocTemplateFiles.clear()
        supportingFiles.clear()

        // clear reserved words not used by our generator
        reservedWords.removeAll(setOf(
            "ApiClient",
            "ApiException",
            "ApiResponse",
            "Configuration",
            "StringUtil",
            "file",
            "object",
            "configuration"
        ))
    }

    override fun processOpts() {
        super.processOpts()
    }

    override fun preprocessOpenAPI(openAPI: OpenAPI) {
        super.preprocessOpenAPI(openAPI)
    }

    override fun postProcessModelProperty(model: CodegenModel, property: CodegenProperty) {
        super.postProcessModelProperty(model, property)

        // support to overwrite the name via x-name-overwrite
        model.vars.filter { p -> p.vendorExtensions.containsKey("x-name-overwrite") }.forEach { p ->
            p.name = p.vendorExtensions["x-name-overwrite"].toString()
        }

        // container inner type for enums
        if (model.isEnum) {
            val matchResult = containerInnerTypePattern.matchEntire(model.dataType)
            val innerType = matchResult?.groupValues?.getOrNull(1) ?: model.dataType
            model.vendorExtensions["x-enum-innerType"] = innerType
        }
        model.vars.filter { p -> p.isEnum }.forEach { p ->
            val matchResult = containerInnerTypePattern.matchEntire(p.dataType)
            val innerType = matchResult?.groupValues?.getOrNull(1) ?: p.dataType
            p.vendorExtensions["x-enum-innerType"] = innerType
        }
    }

    override fun fromModel(name: String, model: Schema<*>): CodegenModel {
        val codegenModel = super.fromModel(name, model)

        return codegenModel
    }

    override fun postProcessOperationsWithModels(objs: OperationsMap, allModels: MutableList<ModelMap>): OperationsMap {
        val modifiedObjs = super.postProcessOperationsWithModels(objs, allModels)
        modifiedObjs.preprocessOperations()

        return modifiedObjs
    }

    /**
     * toVarName calls Camelize which removes _ from the beginning of the input string which is not desired for our use case.
     * <p>
     * This override adds the _ back to the beginning of the output string if the input string starts with _.
     */
    override fun toVarName(input: String): String {
        val output = super.toVarName(input)

        if (input.startsWith("_")) {
            return "_$output"
        }

        return output
    }

    /**
     * packageToPath replaces all dots in the path with a /
     *
     * @param outputFolder
     * @param sourceFolder
     * @param packageName
     * @return
     */
    fun packageToPath(outputFolder: String, sourceFolder: String, packageName: String): String {
        return (outputFolder + File.separator + sourceFolder + File.separator + packageName).replace(".", File.separator)
    }

    /**
     * Creates the output directories if they don't exist.
     *
     * @param directories A list of directories to create.
     */
    fun createOutputDirectories(directories: List<String>) {
        for (directory in directories) {
            val directoryFile = File(directory)
            if (!directoryFile.exists()) {
                if (!directoryFile.mkdirs()) {
                    throw IOException("Failed to create output directory: $directory")
                }
            }
        }
    }
}
