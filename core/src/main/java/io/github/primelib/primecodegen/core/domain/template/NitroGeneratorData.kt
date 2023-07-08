package io.github.primelib.primecodegen.core.domain.template;


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
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
     * OpenAPI Info - Name
     */
    var appName: String? = null,

    /**
     * OpenAPI Info - Version
     */
    var appVersion: String? = null,

    /**
     * OpenAPI Info - Description
     */
    var appDescription: String? = null,
    var infoEmail: String? = null,
    var infoName: String? = null,
    var infoUrl: String? = null,
    var licenseInfo: String? = null,
    var licenseUrl: String? = null,
    var licenseName: String? = null,
    var termsOfService: String? = null,

    var model: NitroGeneratorModelData? = null,

    var models: MutableList<NitroGeneratorModelData> = mutableListOf(),

    var operation: NitroGeneratorOperationData? = null,

    var operations: MutableList<NitroGeneratorOperationData> = mutableListOf(),

    var api: NitroGeneratorApiData? = null,

    var apis: MutableList<NitroGeneratorApiData> = mutableListOf(),
) {
    companion object {
        val OBJECT_MAPPER: ObjectMapper = ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    }

    @JsonIgnore
    fun asMap(): MutableMap<String, Any> {
        return OBJECT_MAPPER.convertValue(this, object : TypeReference<HashMap<String, Any>>() {})
    }

    /**
     * @deprecated the NitroGenerator class needs this because it's still in java.
     */
    @JsonIgnore
    fun getCopy(): NitroGeneratorData {
        return copy()
    }
}
