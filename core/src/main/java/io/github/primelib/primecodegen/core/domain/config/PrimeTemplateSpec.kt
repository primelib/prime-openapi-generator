package io.github.primelib.primecodegen.core.domain.config;

data class PrimeTemplateSpec(
    /**
     * description of the template, used for documentation purposes
     */
    val description: String? = null,
    val sourceTemplate: String,
    val targetDirectory: String,
    val targetFileName: String,
    val scope: TemplateScope,
    val iterator: PrimeIterator = PrimeIterator.ONCE_API,
    val overwrite: Boolean = false
) {
    init {
        requireNotNull(sourceTemplate) { "sourceTemplate is marked non-null but is null" }
        requireNotNull(targetDirectory) { "targetDirectory is marked non-null but is null" }
        requireNotNull(targetFileName) { "targetFileName is marked non-null but is null" }
        requireNotNull(scope) { "scope is marked non-null but is null" }
        requireNotNull(iterator) { "iterator is marked non-null but is null" }
        requireNotNull(overwrite) { "overwrite is marked non-null but is null" }
    }

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
