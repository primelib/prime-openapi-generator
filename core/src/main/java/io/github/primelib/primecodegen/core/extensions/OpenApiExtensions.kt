package io.github.primelib.primecodegen.core.extensions

import io.swagger.v3.oas.models.OpenAPI

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
