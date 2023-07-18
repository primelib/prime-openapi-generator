package io.github.primelib.primecodegen.core.extensions

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.SpecVersion
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter

fun OpenAPI.pruneOperationTags() {
    if (paths != null) {
        for ((pathname, path) in paths.entries) {
            if (path.readOperations() == null) {
                continue
            }
            for (operation in path.readOperations()) {
                if (operation.tags != null && operation.tags.size > 0) {
                    operation.tags = listOf()
                }
            }
        }
    }
}

fun OpenAPI.appendContentTypeHeaderIfMissing() {
    paths?.let { paths ->
        paths.forEach { (path, pathItem) ->
            pathItem.readOperations().forEach { operation ->
                val hasContentTypeParam = operation.parameters?.any { it.`in` == "header" && it.name == "Content-Type" } ?: false
                if (operation.requestBody != null && !hasContentTypeParam) {
                    val suggestedContentType = operation.requestBody?.content?.keys?.firstOrNull() ?: "application/json"

                    val param = Parameter()
                    param.name = "Content-Type"
                    param.`in`("header")
                    param.required = true
                    param.schema = Schema<String>(SpecVersion.V31)
                    param.schema.type = "string"
                    param.schema.setDefault(suggestedContentType)
                    param.addExtension("x-param-static", "true")

                    operation.addParametersItem(param)
                }
            }
        }
    }
}
