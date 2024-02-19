package io.github.primelib.primecodegen.javafeign.config;

import io.github.primelib.primecodegen.core.api.PrimeCodegenConfig;

data class JavaFeignGeneratorConfig(
    var hideGenerationTimestamp: Boolean = true,
    var hideLicense: Boolean = true,
    var serializableModel: Boolean = false,
    var jetbrainsAnnotationsNullable: Boolean = true,
    var requestOverloadSpec: Boolean = false,
) : PrimeCodegenConfig() {
    companion object {
        const val JETBRAINS_ANNOTATION_NULLABLE = "jetbrainsAnnotationsNullable"
        const val REQUEST_OVERLOAD_SPEC = "requestOverloadSpec"

        fun of(data: Map<String, Any>?): JavaFeignGeneratorConfig {
            val cfg = JavaFeignGeneratorConfig()

            // defaults
            cfg.hideGenerationTimestamp = true
            cfg.hideLicense = true
            cfg.serializableModel = false
            cfg.jetbrainsAnnotationsNullable = true
            cfg.requestOverloadSpec = true

            // extract from data
            if (data != null) {
                cfg.requestOverloadSpec = data[REQUEST_OVERLOAD_SPEC] as? Boolean ?: false
            }

            return cfg
        }
    }
}
