package io.github.primelib.primecodegen.core.api;

import io.github.primelib.primecodegen.core.domain.config.PrimeTemplateSpec

/**
 * Base for Advanced Configuration Options
 */
open class PrimeCodegenConfig {
    val templateSpecs = mutableListOf<PrimeTemplateSpec>()
}
