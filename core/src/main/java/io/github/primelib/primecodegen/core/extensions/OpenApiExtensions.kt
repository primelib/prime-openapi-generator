package io.github.primelib.primecodegen.core.extensions

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.SpecVersion
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter

fun OpenAPI.pruneOperationTags() {
    if (paths != null) {
        for ((_, path) in paths.entries) {
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
        paths.forEach { (_, pathItem) ->
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

fun OpenAPI.appendAcceptHeaderIfMissing() {
    paths?.let { paths ->
        paths.forEach { (_, pathItem) ->
            pathItem.readOperations().forEach { operation ->
                val hasAcceptParam = operation.parameters?.any { it.`in` == "header" && it.name == "Accept" } ?: false
                if (operation.responses != null && !hasAcceptParam) {
                    val suggestedAcceptType = operation.responses.values.firstOrNull()?.content?.keys?.firstOrNull() ?: "application/json"

                    val param = Parameter()
                    param.name = "Accept"
                    param.`in`("header")
                    param.required = true
                    param.schema = Schema<String>(SpecVersion.V31)
                    param.schema.type = "string"
                    param.schema.setDefault(suggestedAcceptType)
                    param.addExtension("x-param-static", "true")

                    operation.addParametersItem(param)
                }
            }
        }
    }
}

/**
 * Fix the type of parameters that are arrays but are not marked as such.
 */
fun OpenAPI.fixParamTypes(paramNameTransformer: (String) -> String) {
    paths?.let { paths ->
        val updatedPaths = Paths()

        paths.forEach { (pathUrl, pathItem) ->
            var newUrl = pathUrl

            pathItem.readOperations().forEach { operation ->
                operation.parameters?.forEach { param ->
                    if (param.`in` == "path") {
                        newUrl = newUrl.replace("{" + param.name + "}", "{" + paramNameTransformer.invoke(param.name) + "}")
                    }

                    processParam(param, paramNameTransformer)
                }
            }

            updatedPaths.addPathItem(newUrl, pathItem)
        }

        this.paths = updatedPaths
    }

    components.parameters?.values?.forEach { param ->
        processParam(param, paramNameTransformer)
    }
}

fun processParam(param: Parameter, paranNameTransformer: (String) -> String) {
    param.addExtension("x-base-name", paranNameTransformer.invoke(param.name.removeSuffix("[]")))

    if (param.name.endsWith("[]") && param.explode == true && param.`in` == "query" && !"array".contentEquals(param.schema.type)) {
        // move schema into items
        param.schema.items = Schema<Any>(SpecVersion.V30)
        param.schema.items.type = param.schema.type
        param.schema.items.format = param.schema.format
        param.schema.items.enum = param.schema.enum as List<Nothing>?
        param.schema.items.example = param.schema.example
        param.schema.items.uniqueItems = param.schema.uniqueItems

        // set main schema to array and reset other fields
        param.schema.type = "array"
        param.schema.format = null
        param.schema.enum = null
        param.schema.example = null
        param.schema.uniqueItems = null
    }
}
