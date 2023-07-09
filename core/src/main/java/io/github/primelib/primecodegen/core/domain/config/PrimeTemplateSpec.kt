package io.github.primelib.primecodegen.core.domain.config;

import io.github.primelib.primecodegen.core.domain.template.NitroGeneratorData

data class PrimeTemplateSpec(
    /**
     * description of the template, used for documentation purposes
     */
    val description: String? = null,
    val sourceTemplate: String,
    val targetDirectory: String,
    val targetFileName: String,
    val scope: TemplateScope,
    val iterator: TemplateIterator = TemplateIterator.ONCE_API,
    val overwrite: Boolean = false,
    /**
     * filter is used to skip file generation for certain templates
     */
    val filter: (NitroGeneratorData) -> Boolean = { true },
    /**
     * transform is used to modify the data before it is passed to the template engine
     */
    val transform: (NitroGeneratorData) -> Unit = { },
) {
    /**
     * returns a category key, used to check of file generation should be skipped for this template
     */
    fun getSkippedBy(): String {
        return when (scope) {
            TemplateScope.MODEL -> "models"
            TemplateScope.API -> "apis"
            else -> ""
        }
    }
}
