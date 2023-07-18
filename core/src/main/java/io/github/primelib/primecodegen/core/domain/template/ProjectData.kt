package io.github.primelib.primecodegen.core.domain.template

import org.openapitools.codegen.CodegenConfig

data class ProjectData(
    val name: String,
    val description: String,
    val repository: String,
    val inceptionYear: String,
    val licenseName: String,
    val licenseUrl: String,
    val artifactGroupId: String,
    val artifactId: String,
    val maintainerId: String,
    val maintainerName: String,
    val maintainerEMail: String,
) {
    companion object {
        fun of(config: CodegenConfig): ProjectData {
            return ProjectData(
                name = config.additionalProperties().getOrDefault("projectName", "").toString(),
                description = config.additionalProperties().getOrDefault("projectDescription", "").toString(),
                repository = config.additionalProperties().getOrDefault("projectRepository", "").toString(),
                inceptionYear = config.additionalProperties().getOrDefault("projectInceptionYear", "2023").toString(),
                licenseName = config.additionalProperties().getOrDefault("projectLicenseName", "").toString(),
                licenseUrl = config.additionalProperties().getOrDefault("projectLicenseUrl", "").toString(),
                artifactGroupId = config.additionalProperties().getOrDefault("projectArtifactGroupId", "").toString(),
                artifactId = config.additionalProperties().getOrDefault("projectArtifactId", "").toString(),
                maintainerId = config.additionalProperties().getOrDefault("projectMaintainerId", "").toString(),
                maintainerName = config.additionalProperties().getOrDefault("projectMaintainerName", "").toString(),
                maintainerEMail = config.additionalProperties().getOrDefault("projectMaintainerEMail", "").toString(),
            )
        }
    }
}
