package io.github.primelib.primecodegen.core.domain.template;

import com.fasterxml.jackson.annotation.JsonIgnore
import org.openapitools.codegen.CodegenConfig
import org.openapitools.codegen.CodegenModel

data class NitroGeneratorModelData(
    val packageName: String,
    val classname: String,
    val imports: Collection<NitroGeneratorImport>,
    var importPath: String?,
    val title: String?,
    val description: String?,
    val isEnum: Boolean?,
    var isDeprecated: Boolean?,
    var vendorExtensions: Map<String, Any>?,
    var codegenModel: CodegenModel?
) {
    companion object {
        fun of(data: Map<String, Any>, config: CodegenConfig): NitroGeneratorModelData {
            val response = NitroGeneratorModelData(
                packageName = data["package"] as String,
                classname = data["classname"] as String,
                imports = NitroGeneratorImport.ofList(data["imports"] as List<Map<String, Any>>),
                importPath = null,
                title = data["title"] as? String,
                description = data["description"] as? String,
                isEnum = data["isEnum"] as? Boolean,
                isDeprecated = null,
                vendorExtensions = null,
                codegenModel = null
            )

            val models = data["models"] as? List<Map<String, Any>>
            if (!models.isNullOrEmpty()) {
                val model = models[0]
                response.importPath = model["importPath"] as? String
                response.isDeprecated = data["isDeprecated"] as? Boolean
                response.vendorExtensions = data["vendorExtensions"] as? Map<String, Any>
                response.codegenModel = model["model"] as? CodegenModel
            }

            return response
        }

        fun ofList(data: Collection<Map<String, Any>>, config: CodegenConfig): List<NitroGeneratorModelData> {
            return data.map { of(it, config) }
        }
    }

    @JsonIgnore
    fun asMap(): Map<String, Any?> = mapOf(
        "packageName" to packageName,
        "classname" to classname,
        "imports" to imports,
        "importPath" to importPath,
        "title" to title,
        "description" to description,
        "isEnum" to isEnum,
        "isDeprecated" to isDeprecated,
        "vendorExtensions" to vendorExtensions,
        "codegenModel" to codegenModel
    )
}
