package io.github.primelib.primecodegen.core.domain.template;

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityScheme

data class AuthTemplateData(
    var apiKeyKeyDefault: String = "X-Api-Key",
    var apiKeyLocationDefault: String = "header",
) {
    companion object {
        fun of(openApi: OpenAPI): AuthTemplateData {
            val response = AuthTemplateData()

            openApi.components.securitySchemes.forEach { (name, schema) ->
                if (schema.type == SecurityScheme.Type.APIKEY) {
                    response.apiKeyKeyDefault = schema.name
                    response.apiKeyLocationDefault = schema.`in`.name.lowercase()
                }
            }

            return response
        }
    }
}
