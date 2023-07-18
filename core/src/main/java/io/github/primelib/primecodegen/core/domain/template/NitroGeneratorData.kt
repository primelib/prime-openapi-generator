package io.github.primelib.primecodegen.core.domain.template;


import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.primelib.primecodegen.core.api.PrimeCodegenConfig
import io.swagger.v3.oas.models.OpenAPI

/**
 * base generator data
 */
data class NitroGeneratorData(
    // generator config
    var dryRun: Boolean = false,
    var openAPI: OpenAPI? = null,
    var debugOpenAPI: Boolean = false,
    var generateApis: Boolean = true,
    var generateApiTests: Boolean = true,
    var generateApiDocumentation: Boolean = true,
    var generateModels: Boolean = true,
    var generateModelTests: Boolean = true,
    var generateModelDocumentation: Boolean = true,
    var generateSupportingFiles: Boolean = true,
    var generateSkipFormModel: Boolean = false,
    var generateMetadata: Boolean = true,

    // template config
    var config: PrimeCodegenConfig? = null,

    // generator info
    var generatorVersion: String? = null,
    var generatorDate: String? = null,
    var generatorYear: String? = null,
    var generatorClass: String? = null,
    var inputSpec: String? = null,

    // server
    var contextPath: String? = null,
    var basePathWithoutHost: String? = null,
    var basePath: String? = null,

    // packages
    var apiPackage: String? = null,
    var modelPackage: String? = null,
    var testPackage: String? = null,
    var mainClassName: String? = null,
    var additionalProperties: MutableMap<String, Any> = mutableMapOf(),

    /**
     * OpenAPI - Processed Data
     */
    var details: OpenAPIDetails? = null,
    var project: ProjectData? = null,
    var model: NitroGeneratorModelData? = null,
    var models: MutableList<NitroGeneratorModelData> = mutableListOf(),
    var operation: NitroGeneratorOperationData? = null,
    var operations: MutableList<NitroGeneratorOperationData> = mutableListOf(),
    var api: NitroGeneratorApiData? = null,
    var apis: MutableList<NitroGeneratorApiData> = mutableListOf(),
    var auth: AuthTemplateData? = null,
) {
    @JsonIgnore
    fun asMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "dryRun" to dryRun,
            "openAPI" to openAPI,
            "debugOpenAPI" to debugOpenAPI,
            "generateApis" to generateApis,
            "generateApiTests" to generateApiTests,
            "generateApiDocumentation" to generateApiDocumentation,
            "generateModels" to generateModels,
            "generateModelTests" to generateModelTests,
            "generateModelDocumentation" to generateModelDocumentation,
            "generateSupportingFiles" to generateSupportingFiles,
            "generateSkipFormModel" to generateSkipFormModel,
            "generateMetadata" to generateMetadata,
            "config" to config,
            "generatorVersion" to generatorVersion,
            "generatorDate" to generatorDate,
            "generatorYear" to generatorYear,
            "generatorClass" to generatorClass,
            "inputSpec" to inputSpec,
            "contextPath" to contextPath,
            "basePathWithoutHost" to basePathWithoutHost,
            "basePath" to basePath,
            "apiPackage" to apiPackage,
            "modelPackage" to modelPackage,
            "testPackage" to testPackage,
            "mainClassName" to mainClassName,
            "additionalProperties" to additionalProperties,
            "details" to details,
            "project" to project,
            "model" to model,
            "models" to models,
            "operation" to operation,
            "operations" to operations,
            "api" to api,
            "apis" to apis,
            "auth" to auth,
        )
    }

    /**
     * generates a deep copy of the template data to transform on a per-template basis
     */
    @JsonIgnore
    fun getCopy(): NitroGeneratorData {
        return copy(
            additionalProperties = additionalProperties.toMutableMap(),
            details = details?.copy(),
            project = project?.copy(),
            model = model?.copy(),
            models = models.map { it.copy() }.toMutableList(),
            operation = operation?.copy(),
            operations = operations.map { it.copy() }.toMutableList(),
            api = api?.copy(),
            apis = apis.map { it.copy() }.toMutableList(),
            auth = auth?.copy(),
        )
    }
}
