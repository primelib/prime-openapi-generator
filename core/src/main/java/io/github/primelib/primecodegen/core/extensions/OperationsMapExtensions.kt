package io.github.primelib.primecodegen.core.extensions

import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.model.OperationsMap

@Suppress("NestedBlockDepth", "UNCHECKED_CAST")
fun OperationsMap.preprocessOperations(): OperationsMap {
    val operations = this["operations"] as Map<String, Any>
    val operationList = operations["operation"] as List<CodegenOperation>

    operationList.forEach { op ->
        if (op.hasAuthMethods) {
            val requiredScopes = op.authMethods.flatMap { it.scopes.orEmpty() }
                .mapNotNull { it["scope"] as? String }
                .toSet()
            op.vendorExtensions["x-required-scopes"] = requiredScopes
        }
    }

    return this
}
