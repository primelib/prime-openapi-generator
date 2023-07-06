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

import java.util.Set;

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

        // spec package
        final String specFolder = JavaCodegenUtils.packageToPath(outputFolder, sourceFolder, modelPackage.substring(0, modelPackage.lastIndexOf(".")) + ".spec");
        additionalProperties.put("specPackage", modelPackage.substring(0, modelPackage.lastIndexOf(".")) + ".spec");

        // ensure directories exist
        GeneratorUtils.createOutputDirectories(Set.of(invokerFolder, apiFolder, modelFolder, specFolder));

        // nitro files
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api.peb").targetDirectory(apiFileFolder()).targetFileName("{name}.java").scope(NitroScope.API).iterator(NitroIterator.EACH_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_test.peb").targetDirectory(apiTestFileFolder()).targetFileName("{name}.java").scope(NitroScope.API_TEST).iterator(NitroIterator.EACH_API).overwrite(false).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("model.peb").targetDirectory(modelFileFolder()).targetFileName("{name}.java").scope(NitroScope.MODEL).iterator(NitroIterator.EACH_MODEL).build());

        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_main.peb").targetDirectory(apiFileFolder()).targetFileName("{mainClassName}.java").scope(NitroScope.API).iterator(NitroIterator.ONCE_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_collect.peb").targetDirectory(apiFileFolder()).targetFileName("{mainClassName}ApiCollection.java").scope(NitroScope.API).iterator(NitroIterator.ONCE_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_client.peb").targetDirectory(invokerFolder).targetFileName("{mainClassName}Client.java").scope(NitroScope.API).iterator(NitroIterator.ONCE_API).build());

        // - spec files
        if (cfg.getRequestOverloadSpec()) {
            cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_spec.peb").targetDirectory(specFolder).targetFileName("{name}Spec.java").scope(NitroScope.MODEL).iterator(NitroIterator.EACH_API_OPERATION).build());
        }
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
