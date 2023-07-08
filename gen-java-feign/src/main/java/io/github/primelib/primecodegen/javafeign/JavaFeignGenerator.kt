package io.github.primelib.primecodegen.javafeign;

import io.github.primelib.primecodegen.core.api.PrimeCodegenBase
import io.github.primelib.primecodegen.core.api.PrimeCodegenConfig
import io.github.primelib.primecodegen.core.domain.config.PrimeIterator
import io.github.primelib.primecodegen.core.domain.config.PrimeTemplateSpec
import io.github.primelib.primecodegen.core.domain.config.TemplateScope
import io.github.primelib.primecodegen.core.generator.ExtendableJavaCodegenBase
import io.github.primelib.primecodegen.javafeign.config.JavaFeignGeneratorConfig
import org.openapitools.codegen.CodegenConfig
import org.openapitools.codegen.CodegenType

class JavaFeignGenerator : ExtendableJavaCodegenBase(), CodegenConfig, PrimeCodegenBase {
    companion object {
        private const val CODEGEN_NAME = "prime-client-java-feign"
        private const val CODEGEN_HELP = "Generates a $CODEGEN_NAME client library."
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

    override fun processOpts() {
        super.processOpts()

        // directories
        val invokerFolder = packageToPath(outputFolder, sourceFolder, invokerPackage)
        val apiFolder = packageToPath(outputFolder, sourceFolder, apiPackage)
        val modelFolder = packageToPath(outputFolder, sourceFolder, modelPackage)

        // custom package
        val specFolder = packageToPath(outputFolder, sourceFolder, modelPackage.substringBeforeLast(".") + ".spec")
        additionalProperties["specPackage"] = modelPackage.substringBeforeLast(".") + ".spec"
        additionalProperties["invokerPackage"] = modelPackage.substringBeforeLast(".")

        // ensure directories exist
        createOutputDirectories(listOf(invokerFolder, apiFolder, modelFolder, specFolder))

        // api files
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a global factory class to instantiate all api classes",
            sourceTemplate = "api_factory.peb",
            targetDirectory = invokerFolder,
            targetFileName = "{mainClassName}Factory.java",
            scope = TemplateScope.API,
            iterator = PrimeIterator.ONCE_API,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a configuration spec that can be passed to the factory, customizing the api client",
            sourceTemplate = "api_factoryspec.peb",
            targetDirectory = invokerFolder,
            targetFileName = "{mainClassName}FactorySpec.java",
            scope = TemplateScope.API,
            iterator = PrimeIterator.ONCE_API,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "the main api class, containing all api operations",
            sourceTemplate = "api_main.peb",
            targetDirectory = apiFolder,
            targetFileName = "{mainClassName}.java",
            scope = TemplateScope.API,
            iterator = PrimeIterator.ONCE_API,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a collection of all api classes",
            sourceTemplate = "api_collect.peb",
            targetDirectory = apiFolder,
            targetFileName = "{mainClassName}ApiCollection.java",
            scope = TemplateScope.API,
            iterator = PrimeIterator.ONCE_API,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a module class containing all api operations for a single path",
            sourceTemplate = "api_module.peb",
            targetDirectory = apiFolder,
            targetFileName = "{name}.java",
            scope = TemplateScope.API,
            iterator = PrimeIterator.EACH_API,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a module test class containing all api operations for a single path",
            sourceTemplate = "api_module_test.peb",
            targetDirectory = apiTestFileFolder(),
            targetFileName = "{name}.java",
            scope = TemplateScope.API_TEST,
            iterator = PrimeIterator.EACH_API,
        ))

        // model files
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a model class representing a single api response",
            sourceTemplate = "model.peb",
            targetDirectory = modelFolder,
            targetFileName = "{name}.java",
            scope = TemplateScope.MODEL,
            iterator = PrimeIterator.EACH_MODEL,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            description = "a spec equivalent to the model class",
            sourceTemplate = "api_spec.peb",
            targetDirectory = modelTestFileFolder(),
            targetFileName = "{name}Spec.java",
            scope = TemplateScope.MODEL,
            iterator = PrimeIterator.EACH_API_OPERATION,
        ))

        // supporting files
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "gitignore.peb",
            targetDirectory = outputFolder,
            targetFileName = ".gitignore",
            scope = TemplateScope.SUPPORT,
        ))
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
            overwrite = false,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "gradle/gradlew.peb",
            targetDirectory = outputFolder,
            targetFileName = "gradlew",
            scope = TemplateScope.SUPPORT,
            overwrite = false,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "gradle/gradlew.bat.peb",
            targetDirectory = outputFolder,
            targetFileName = "gradlew.bat",
            scope = TemplateScope.SUPPORT,
            overwrite = false,
        ))
        cfg.templateSpecs.add(PrimeTemplateSpec(
            sourceTemplate = "renovate.json.peb",
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
