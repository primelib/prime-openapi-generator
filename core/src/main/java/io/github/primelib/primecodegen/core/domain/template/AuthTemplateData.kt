package io.github.primelib.primecodegen.core.domain.template;

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityScheme

data class AuthTemplateData(
    var hasApiKey: Boolean = false,
    var hasBasic: Boolean = false,
    var hasBearer: Boolean = false,
    var apiKeyKeyDefault: String = "X-Api-Key",
    var apiKeyLocationDefault: String = "header",
) {
    companion object {
        fun of(openApi: OpenAPI): AuthTemplateData {
            val response = AuthTemplateData()

            openApi.components.securitySchemes?.forEach { (name, schema) ->
                if (schema.type == SecurityScheme.Type.APIKEY) {
                    response.hasApiKey = true
                    response.apiKeyKeyDefault = schema.name
                    response.apiKeyLocationDefault = schema.`in`.name.lowercase()
                }
                if (schema.type == SecurityScheme.Type.HTTP && schema.scheme == "basic") {
                    response.hasBasic = true
                }
                if (schema.type == SecurityScheme.Type.HTTP && schema.scheme == "bearer") {
                    response.hasBearer = true
                }
                if ((schema.type == SecurityScheme.Type.OAUTH2 || schema.type == SecurityScheme.Type.OPENIDCONNECT)) {
                    response.hasBearer = true
                }
            }

            return response
        }
    }
}
