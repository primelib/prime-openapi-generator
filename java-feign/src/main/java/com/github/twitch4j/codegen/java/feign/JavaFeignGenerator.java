package com.github.twitch4j.codegen.java.feign;

import com.github.twitch4j.codegen.core.api.INitroCodegen;
import com.github.twitch4j.codegen.core.domain.config.NitroCodegenFile;
import com.github.twitch4j.codegen.core.domain.config.NitroIterator;
import com.github.twitch4j.codegen.core.domain.config.NitroScope;
import com.github.twitch4j.codegen.core.generator.AbstractCleanJavaCodegen;
import com.github.twitch4j.codegen.core.util.GeneratorUtils;
import com.github.twitch4j.codegen.java.feign.config.JavaFeignGeneratorConfig;
import com.github.twitch4j.codegen.java.feign.utils.JavaCodegenUtils;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenType;

import java.util.List;

@Slf4j
public class JavaFeignGenerator extends AbstractCleanJavaCodegen implements CodegenConfig, INitroCodegen {
    protected static String CODEGEN_NAME = "nitro-java-feign";
    protected static String CODEGEN_HELP = "Generates a " + CODEGEN_NAME + " client library.";

    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    public String getName() {
        return CODEGEN_NAME;
    }

    public String getHelp() {
        return CODEGEN_HELP;
    }

    @Accessors(fluent = true)
    @Getter
    private final JavaFeignGeneratorConfig cfg;

    public JavaFeignGenerator() {
        super();

        // setup
        templateDir = CODEGEN_NAME;
        this.cfg = JavaFeignGeneratorConfig.of(null);
    }

    @Override
    public void processOpts() {
        super.processOpts();

        // directories
        final String invokerFolder = JavaCodegenUtils.packageToPath(outputFolder, sourceFolder, invokerPackage);
        final String apiFolder = JavaCodegenUtils.packageToPath(outputFolder, sourceFolder, apiPackage);
        final String modelFolder = JavaCodegenUtils.packageToPath(outputFolder, sourceFolder, modelPackage);

        // custom package
        final String specFolder = JavaCodegenUtils.packageToPath(outputFolder, sourceFolder, modelPackage.substring(0, modelPackage.lastIndexOf(".")) + ".spec");
        additionalProperties.put("specPackage", modelPackage.substring(0, modelPackage.lastIndexOf(".")) + ".spec");
        additionalProperties.put("invokerPackage", modelPackage.substring(0, modelPackage.lastIndexOf(".")));

        // ensure directories exist
        GeneratorUtils.createOutputDirectories(List.of(invokerFolder, apiFolder, modelFolder, specFolder));

        // api files
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_factory.peb").targetDirectory(invokerFolder).targetFileName("{mainClassName}Factory.java").scope(NitroScope.API).iterator(NitroIterator.ONCE_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_factoryspec.peb").targetDirectory(invokerFolder).targetFileName("{mainClassName}FactorySpec.java").scope(NitroScope.API).iterator(NitroIterator.ONCE_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_main.peb").targetDirectory(apiFileFolder()).targetFileName("{mainClassName}.java").scope(NitroScope.API).iterator(NitroIterator.ONCE_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_collect.peb").targetDirectory(apiFileFolder()).targetFileName("{mainClassName}ApiCollection.java").scope(NitroScope.API).iterator(NitroIterator.ONCE_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_module.peb").targetDirectory(apiFileFolder()).targetFileName("{name}.java").scope(NitroScope.API).iterator(NitroIterator.EACH_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_module_test.peb").targetDirectory(apiTestFileFolder()).targetFileName("{name}.java").scope(NitroScope.API_TEST).iterator(NitroIterator.EACH_API).overwrite(false).build());

        // model files
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("model.peb").targetDirectory(modelFileFolder()).targetFileName("{name}.java").scope(NitroScope.MODEL).iterator(NitroIterator.EACH_MODEL).build());
        if (cfg.getRequestOverloadSpec()) {
            cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_spec.peb").targetDirectory(specFolder).targetFileName("{name}Spec.java").scope(NitroScope.MODEL).iterator(NitroIterator.EACH_API_OPERATION).build());
        }

        // supporting files
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("gradle/build.gradle.kts.peb").targetDirectory(outputFolder).targetFileName("build.gradle.kts").scope(NitroScope.SUPPORT).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("gradle/settings.gradle.kts.peb").targetDirectory(outputFolder).targetFileName("settings.gradle.kts").scope(NitroScope.SUPPORT).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("gradle/gradle-wrapper.properties.peb").targetDirectory(outputFolder).targetFileName("gradle/wrapper/gradle-wrapper.properties").scope(NitroScope.SUPPORT).overwrite(false).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("gradle/gradlew.peb").targetDirectory(outputFolder).targetFileName("gradlew").scope(NitroScope.SUPPORT).overwrite(false).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("gradle/gradlew.bat.peb").targetDirectory(outputFolder).targetFileName("gradlew.bat").scope(NitroScope.SUPPORT).overwrite(false).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("renovate.json.peb").targetDirectory(outputFolder).targetFileName("renovate.json").scope(NitroScope.SUPPORT).build());
    }

    /**
     * Log Output
     */
    @Override
    public void postProcess() {
        System.out.println("################################################################################");
        System.out.println("# Thanks for using OpenAPI Generator.                                          #");
        System.out.println("#                                                                              #");
        System.out.println("# This generator's contributed by github.com/twitch4j                          #");
        System.out.println("################################################################################");
    }

}
