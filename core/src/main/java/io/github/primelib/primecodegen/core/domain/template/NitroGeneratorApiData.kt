package io.github.primelib.primecodegen.core.domain.template;

import com.fasterxml.jackson.annotation.JsonIgnore
import org.openapitools.codegen.CodegenConfig
import org.openapitools.codegen.CodegenOperation

data class NitroGeneratorApiData(
    val packageName: String,
    val classname: String,
    val imports: MutableList<NitroGeneratorImport>,
    val operations: MutableList<CodegenOperation>,
    val primeOperations: MutableList<NitroGeneratorOperationData> = mutableListOf(),
) {
    companion object {
        fun of(api: MutableMap<String, Any>, config: CodegenConfig): NitroGeneratorApiData {
            val apiData = api["operations"] as MutableMap<String, Any>

            return NitroGeneratorApiData(
                packageName = api["package"] as String,
                classname = apiData["classname"] as String,
                imports = NitroGeneratorImport.ofList(api["imports"] as MutableList<Map<String, Any>>),
                operations = apiData["operation"] as MutableList<CodegenOperation>,
                primeOperations = NitroGeneratorOperationData.ofList(apiData["operation"] as MutableList<CodegenOperation>, config).toMutableList(),
            )
        }

        fun ofList(data: MutableCollection<MutableMap<String, Any>>, config: CodegenConfig): List<NitroGeneratorApiData> {
            return data.map { of(it, config) }
        }
    }
}
