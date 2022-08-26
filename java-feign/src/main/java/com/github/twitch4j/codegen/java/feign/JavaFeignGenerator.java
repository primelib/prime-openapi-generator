package com.github.twitch4j.codegen.java.feign;

import com.github.twitch4j.codegen.core.api.INitroCodegen;
import com.github.twitch4j.codegen.core.domain.config.NitroCodegenFile;
import com.github.twitch4j.codegen.core.domain.config.NitroIterator;
import com.github.twitch4j.codegen.core.domain.config.NitroScope;
import com.github.twitch4j.codegen.java.feign.config.JavaFeignGeneratorConfig;
import com.github.twitch4j.codegen.java.feign.utils.JavaCodegenUtils;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.CodegenType;
import org.openapitools.codegen.languages.AbstractJavaCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.openapitools.codegen.utils.StringUtils.camelize;

@Slf4j
public class JavaFeignGenerator extends AbstractJavaCodegen implements CodegenConfig, INitroCodegen {

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
    private JavaFeignGeneratorConfig cfg;

    public JavaFeignGenerator() {
        super();

        // setup
        templateDir = CODEGEN_NAME;
        this.cfg = JavaFeignGeneratorConfig.of(null);

        // reset / reconfigure base generator
        modelTemplateFiles.clear();
        apiTemplateFiles.clear();
        apiTestTemplateFiles.clear();
        modelDocTemplateFiles.clear();
        apiDocTemplateFiles.clear();
        importMapping.remove("ApiModelProperty", "io.swagger.annotations.ApiModelProperty");
        importMapping.remove("ApiModel", "io.swagger.annotations.ApiModel");
        setDateLibrary("java8");
    }

    @Override
    public void processOpts() {
        super.processOpts();

        // date library
        if (getDateLibrary() == "java8") {
            // use instant
            typeMapping.put("date", "Instant");
            importMapping.put("Instant", "java.time.Instant");
        }

        // feign main class
        additionalProperties.put("mainClassName", camelize(openAPI.getInfo().getTitle(), false).replace(" ", ""));

        // directories
        final String invokerFolder = JavaCodegenUtils.packageToPath(sourceFolder, invokerPackage);
        final String apiFolder = JavaCodegenUtils.packageToPath(sourceFolder, apiPackage);
        final String modelFolder = JavaCodegenUtils.packageToPath(sourceFolder, modelPackage);
        final String specFolder = (invokerFolder + "/spec");

        // nitro files
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api.peb").targetDirectory(apiFileFolder()).targetFileName("{name}.java").scope(NitroScope.API).iterator(NitroIterator.EACH_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_test.peb").targetDirectory(apiTestFileFolder()).targetFileName("{name}.java").scope(NitroScope.API_TEST).iterator(NitroIterator.EACH_API).overwrite(false).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("model.peb").targetDirectory(modelFileFolder()).targetFileName("{name}.java").scope(NitroScope.MODEL).iterator(NitroIterator.EACH_MODEL).build());

        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_main.peb").targetDirectory(apiFileFolder()).targetFileName(additionalProperties.get("mainClassName")+".java").scope(NitroScope.API).iterator(NitroIterator.ONCE_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_collect.peb").targetDirectory(apiFileFolder()).targetFileName(additionalProperties.get("mainClassName")+"ApiCollection.java").scope(NitroScope.API).iterator(NitroIterator.ONCE_API).build());
        cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_client.peb").targetDirectory(sourceFolder + File.separator + invokerFolder).targetFileName(additionalProperties.get("mainClassName")+"Client.java").scope(NitroScope.API).iterator(NitroIterator.ONCE_API).build());

        // - spec files
        if (cfg.getRequestOverloadSpec()) {
            cfg.addNitroFile(NitroCodegenFile.builder().sourceTemplate("api_spec.peb").targetDirectory(modelFileFolder()).targetFileName("{name}Spec.java").scope(NitroScope.MODEL).iterator(NitroIterator.EACH_API_OPERATION).build());
        }
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);

        // clear legacy imports from base codegen
        model.imports.remove("ApiModelProperty");
        model.imports.remove("ApiModel");
    }

    @Override
    public CodegenModel fromModel(String name, Schema model) {
        CodegenModel codegenModel = super.fromModel(name, model);

        // clear legacy imports from base codegen
        codegenModel.imports.remove("ApiModel");
        codegenModel.imports.remove("org.threeten.bp.LocalDate");

        return codegenModel;
    }

    @Override
    public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {
        objs = super.postProcessOperationsWithModels(objs, allModels);

        Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
        List<CodegenOperation> operationList = (List<CodegenOperation>) operations.get("operation");
        for (CodegenOperation op : operationList) {
            // collect all required scopes and store them into vendor extension
            if (op.hasAuthMethods) {
                Set<String> requiredScopes = new HashSet<>();
                op.authMethods.forEach(method -> {
                    Optional.ofNullable(method.scopes).ifPresent(scope -> scope.forEach(s -> requiredScopes.add(s.get("scope").toString())));
                });
                op.vendorExtensions.put("x-required-scopes", requiredScopes);
            }
        }
        return objs;
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
