package io.github.primelib.primecodegen.core.generator

import io.github.primelib.primecodegen.core.api.PrimeCodegenBase
import io.github.primelib.primecodegen.core.extensions.preprocessOperations
import io.github.primelib.primecodegen.core.postprocess.JavaImportPostProcessor
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import org.openapitools.codegen.CodegenConfig
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenProperty
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
abstract class ExtendableJavaCodegenBase : AbstractJavaCodegen(), CodegenConfig, PrimeCodegenBase {
    private val containerInnerTypePattern = Regex("""^.*<(.+)>$""")

    init {
        modelTemplateFiles.clear()
        apiTemplateFiles.clear()
        apiTestTemplateFiles.clear()
        modelDocTemplateFiles.clear()
        apiDocTemplateFiles.clear()
        supportingFiles.clear()
        importMapping.remove("ApiModelProperty", "io.swagger.annotations.ApiModelProperty")
        importMapping.remove("ApiModel", "io.swagger.annotations.ApiModel")
        importMapping.remove("DateTime")
        importMapping.remove("LocalDateTime")
        importMapping.remove("LocalDate")
        importMapping.remove("LocalTime")
        dateLibrary = "java8"
        typeMapping["date"] = "Instant"
        importMapping["Instant"] = "java.time.Instant"

        // clear reserved words not used by our generator
        reservedWords.removeAll(setOf("ApiClient", "ApiException", "ApiResponse", "Configuration", "StringUtil"))
    }

    override fun processOpts() {
        super.processOpts()

        // mainClassName
        // additionalProperties.put("mainClassName", camelize(Objects.toString(additionalProperties.get("mainClassName").toString(), "default"), CamelizeOption.UPPERCASE_FIRST_CHAR))
    }

    override fun preprocessOpenAPI(openAPI: OpenAPI) {
        super.preprocessOpenAPI(openAPI)
    }

    override fun postProcessModelProperty(model: CodegenModel, property: CodegenProperty) {
        super.postProcessModelProperty(model, property)

        // clear legacy imports from base codegen
        model.imports.remove("ApiModelProperty")
        model.imports.remove("ApiModel")

        // support to overwrite the name via x-alias
        model.vars.filter { p -> p.vendorExtensions.containsKey("x-alias") }.forEach { p ->
            p.name = p.vendorExtensions["x-alias"].toString()
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

        // clear legacy imports from base codegen
        codegenModel.imports.remove("ApiModel")
        codegenModel.imports.remove("org.threeten.bp.LocalDate")

        return codegenModel
    }

    override fun postProcessOperationsWithModels(objs: OperationsMap, allModels: MutableList<ModelMap>): OperationsMap {
        val modifiedObjs = super.postProcessOperationsWithModels(objs, allModels)
        modifiedObjs.preprocessOperations()
        return modifiedObjs
    }

    override fun postProcessFile(file: File?, fileType: String?) {
        super.postProcessFile(file, fileType)
        if (file == null) {
            return
        }

        // process all files with dart extension
        if ("java" == file.extension) {
            var content = file.readText()
            content = JavaImportPostProcessor.removeUnusedImports(content)
            file.writeText(content)
        }
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
