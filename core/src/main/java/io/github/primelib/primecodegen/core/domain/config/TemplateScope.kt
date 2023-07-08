package io.github.primelib.primecodegen.core.domain.config;

enum class TemplateScope {
    MODEL,
    MODEL_TEST,
    MODEL_DOCS,
    API,
    API_TEST,
    API_DOCS,
    SUPPORT;

    /**
     * returns the openapi generator post process type
     */
    val postProcessType: String
        get() {
            return when (this) {
                MODEL -> "model"
                MODEL_TEST -> "model-test"
                MODEL_DOCS -> "model-doc"
                API -> "api"
                API_TEST -> "api-test"
                API_DOCS -> "api-doc"
                else -> ""
            }
        }
}
