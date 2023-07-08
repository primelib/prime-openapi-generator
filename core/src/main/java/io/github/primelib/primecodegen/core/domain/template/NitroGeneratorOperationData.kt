package io.github.primelib.primecodegen.core.domain.template;

import com.fasterxml.jackson.annotation.JsonIgnore
import org.openapitools.codegen.CodegenConfig
import org.openapitools.codegen.CodegenOperation

data class NitroGeneratorOperationData(
    val packageName: String,
    val classname: String,
    val imports: Collection<NitroGeneratorImport>,
    val importPath: String?,
    val codegenOperation: CodegenOperation
) {
    companion object {
        fun of(data: CodegenOperation, config: CodegenConfig): NitroGeneratorOperationData {
            return NitroGeneratorOperationData(
                packageName = config.modelPackage(),
                classname = config.toModelName(data.operationId),
                imports = emptyList(),
                importPath = null,
                codegenOperation = data
            )
        }

        fun ofList(data: Collection<CodegenOperation>, config: CodegenConfig): List<NitroGeneratorOperationData> {
            return data.map { of(it, config) }
        }
    }

    @JsonIgnore
    fun asMap(): Map<String, Any?> {
        return mapOf(
            "packageName" to packageName,
            "classname" to classname,
            "imports" to imports,
            "importPath" to importPath,
            "codegenOperation" to codegenOperation,
        )
    }
}
