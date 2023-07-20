package io.github.primelib.primecodegen.javafeign;

import io.github.primelib.primecodegen.core.api.PrimeCodegenBase
import io.github.primelib.primecodegen.core.api.PrimeCodegenConfig
import io.github.primelib.primecodegen.core.domain.config.PrimeTemplateSpec
import io.github.primelib.primecodegen.core.domain.config.TemplateIterator
import io.github.primelib.primecodegen.core.domain.config.TemplateScope
import io.github.primelib.primecodegen.core.extensions.appendAcceptHeaderIfMissing
import io.github.primelib.primecodegen.core.extensions.appendContentTypeHeaderIfMissing
import io.github.primelib.primecodegen.core.extensions.fixParamArrayType
import io.github.primelib.primecodegen.core.extensions.pruneOperationTags
import io.github.primelib.primecodegen.core.generator.ExtendableJavaCodegenBase
import io.github.primelib.primecodegen.javafeign.config.JavaFeignGeneratorConfig
import io.swagger.v3.oas.models.OpenAPI
import org.openapitools.codegen.CodegenConfig
import org.openapitools.codegen.CodegenType

class JavaFeignGenerator : ExtendableJavaCodegenBase(), CodegenConfig, PrimeCodegenBase {
    companion object {
        const val CODEGEN_NAME = "prime-client-java-feign"
        const val CODEGEN_HELP = "Generates a $CODEGEN_NAME client library."
    }

    override fun getTag(): CodegenType {
        return CodegenType.CLIENT
    }

    override fun getName(): String {
        return CODEGEN_NAME
    }

    override fun getHelp(): String {
        return CODEGEN_HELP
    }

    private val cfg: JavaFeignGeneratorConfig = JavaFeignGeneratorConfig.of(null)

    /**
     * setup
     */
    init {
        templateDir = CODEGEN_NAME
    }

    override fun preprocessOpenAPI(openAPI: OpenAPI) {
        super.preprocessOpenAPI(openAPI)

        // truncate tags to merge all operations into one feign interface
        openAPI.pruneOperationTags()
        openAPI.appendContentTypeHeaderIfMissing()
        openAPI.appendAcceptHeaderIfMissing()
        openAPI.fixParamArrayType()
    }

    override fun processOpts() {
        super.processOpts()

        // directories
        val invokerFolder = packageToPath(outputFolder, sourceFolder, invokerPackage)
        val apiFolder = packageToPath(outputFolder, sourceFolder, apiPackage)
        val modelFolder = packageToPath(outputFolder, sourceFolder, modelPackage)

        // custom package
        val specFolder = packageToPath(outputFolder, sourceFolder, modelPackage.substringBeforeLast(".") + ".spec")
        val authFolder = packageToPath(outputFolder, sourceFolder, modelPackage.substringBeforeLast(".") + ".auth")
        additionalProperties["specPackage"] = modelPackage.substringBeforeLast(".") + ".spec"
        additionalProperties["authPackage"] = modelPackage.substringBeforeLast(".") + ".auth"
        additionalProperties["invokerPackage"] = modelPackage.substringBeforeLast(".")

        // ensure directories exist
        createOutputDirectories(listOf(invokerFolder, apiFolder, modelFolder, specFolder, authFolder))

        // factory
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a global factory class to instantiate all api classes",
            sourceTemplate = "api_factory.peb",
            targetDirectory = invokerFolder,
            targetFileName = "{mainClassName}Factory.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.ONCE_API,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a configuration spec that can be passed to the factory, customizing the api client",
            sourceTemplate = "api_factoryspec.peb",
            targetDirectory = invokerFolder,
            targetFileName = "{mainClassName}FactorySpec.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.ONCE_API,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "proxy spec",
            sourceTemplate = "proxy/proxySpec.peb",
            targetDirectory = invokerFolder,
            targetFileName = "{mainClassName}ProxySpec.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.ONCE_API,
        ))

        // api
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "primary api interface",
            sourceTemplate = "api_main.peb",
            targetDirectory = apiFolder,
            targetFileName = "{mainClassName}Api.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "consumer spec variant of the primary api interface",
            sourceTemplate = "api_mainspec.peb",
            targetDirectory = apiFolder,
            targetFileName = "{mainClassName}ConsumerApi.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
        ))

        // rxjava api
        /*
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "api interface for rxjava",
            sourceTemplate = "api_main.peb",
            targetDirectory = apiFolder,
            targetFileName = "{mainClassName}RxJavaApi.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
            transform = { data ->
                data.mainClassName = "${data.mainClassName}RxJava"
                data.additionalProperties["primeWrapReturnType"] = "Flowable<%s>"
                data.api?.imports?.add(NitroGeneratorImport("io.reactivex.Flowable"))
            }
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "spec variant of the primary api interface",
            sourceTemplate = "api_mainspec.peb",
            targetDirectory = apiFolder,
            targetFileName = "{mainClassName}RxJavaSpecApi.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
            transform = { data ->
                data.mainClassName = "${data.mainClassName}RxJava"
                data.additionalProperties["primeWrapReturnType"] = "Flowable<%s>"
                data.api?.imports?.add(NitroGeneratorImport("io.reactivex.Flowable"))
            }
        ))
        */

        // reactor api
        /*
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "api interface for reactor",
            sourceTemplate = "api_main.peb",
            targetDirectory = apiFolder,
            targetFileName = "{mainClassName}ReactorApi.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
            transform = { data ->
                data.mainClassName = "${data.mainClassName}Reactor"
                data.additionalProperties["primeWrapReturnType"] = "Mono<%s>"
                data.api?.imports?.addAll(listOf(NitroGeneratorImport("reactor.core.publisher.Mono"), NitroGeneratorImport("reactor.core.publisher.Flux")))
            }
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "spec variant of the primary api interface",
            sourceTemplate = "api_mainspec.peb",
            targetDirectory = apiFolder,
            targetFileName = "{mainClassName}ReactorSpecApi.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
            transform = { data ->
                data.mainClassName = "${data.mainClassName}Reactor"
                data.additionalProperties["primeWrapReturnType"] = "Mono<%s>"
                data.api?.imports?.addAll(listOf(NitroGeneratorImport("reactor.core.publisher.Mono"), NitroGeneratorImport("reactor.core.publisher.Flux")))
            }
        ))
        */

        // model files
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a model class representing a single api response",
            sourceTemplate = "model.peb",
            targetDirectory = modelFolder,
            targetFileName = "{name}.java",
            scope = TemplateScope.MODEL,
            iterator = TemplateIterator.EACH_MODEL,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a model class representing a single api response",
            sourceTemplate = "api_operation_spec.peb",
            targetDirectory = specFolder,
            targetFileName = "{name}OperationSpec.java",
            scope = TemplateScope.MODEL,
            iterator = TemplateIterator.EACH_API_OPERATION,
        ))

        // auth files
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "auth method interface",
            sourceTemplate = "auth/authMethod.peb",
            targetDirectory = authFolder,
            targetFileName = "AuthMethod.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "feign authentication interceptor",
            sourceTemplate = "auth/authInterceptor.peb",
            targetDirectory = authFolder,
            targetFileName = "AuthInterceptor.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "auth method - apiKey",
            sourceTemplate = "auth/apiKeyAuthSpec.peb",
            targetDirectory = authFolder,
            targetFileName = "ApiKeyAuthSpec.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
            filter = { data -> data.auth?.hasApiKey ?: false },
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "auth method - basic",
            sourceTemplate = "auth/basicAuthSpec.peb",
            targetDirectory = authFolder,
            targetFileName = "BasicAuthSpec.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
            filter = { data -> data.auth?.hasBasic ?: false },
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "auth method - bearer token",
            sourceTemplate = "auth/bearerAuthSpec.peb",
            targetDirectory = authFolder,
            targetFileName = "BearerAuthSpec.java",
            scope = TemplateScope.API,
            iterator = TemplateIterator.EACH_API,
            filter = { data -> data.auth?.hasBearer ?: false },
        ))

        // supporting files
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "gradle/build.gradle.kts.peb",
            targetDirectory = outputFolder,
            targetFileName = "build.gradle.kts",
            scope = TemplateScope.SUPPORT,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "gradle/settings.gradle.kts.peb",
            targetDirectory = outputFolder,
            targetFileName = "settings.gradle.kts",
            scope = TemplateScope.SUPPORT,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "gradle/gradle-wrapper.properties.peb",
            targetDirectory = outputFolder,
            targetFileName = "gradle/wrapper/gradle-wrapper.properties",
            scope = TemplateScope.SUPPORT,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "gradle/gradlew.peb",
            targetDirectory = outputFolder,
            targetFileName = "gradlew",
            scope = TemplateScope.SUPPORT,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "gradle/gradlew.bat.peb",
            targetDirectory = outputFolder,
            targetFileName = "gradlew.bat",
            scope = TemplateScope.SUPPORT,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "support/gitignore.peb",
            targetDirectory = outputFolder,
            targetFileName = ".gitignore",
            scope = TemplateScope.SUPPORT,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "support/renovate.json.peb",
            targetDirectory = outputFolder,
            targetFileName = "renovate.json",
            scope = TemplateScope.SUPPORT,
        ))
    }

    override fun postProcess() {
        println("################################################################################");
        println("# Thanks for using the PrimeLib OpenAPI Generator.                             #");
        println("#                                                                              #");
        println("# Generator: java8 feign                                                       #");
        println("################################################################################");
    }

    override fun cfg(): PrimeCodegenConfig {
        return cfg
    }
}
