package io.github.primelib.primecodegen.core.generator;

import io.github.primelib.primecodegen.core.api.INitroCodegen;
import io.github.primelib.primecodegen.core.util.ApiSpecUtil;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.AbstractJavaCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import java.util.List;

/**
 * AbstractCleanJavaCodegen
 * <p>
 * This is a clean version of the AbstractJavaCodegen, which has all generated templates and legacy imports removed.
 */
@Slf4j
public abstract class AbstractCleanJavaCodegen extends AbstractJavaCodegen implements CodegenConfig, INitroCodegen {

    public AbstractCleanJavaCodegen() {
        super();

        // reset / reconfigure base generator
        modelTemplateFiles.clear();
        apiTemplateFiles.clear();
        apiTestTemplateFiles.clear();
        modelDocTemplateFiles.clear();
        apiDocTemplateFiles.clear();
        importMapping.remove("ApiModelProperty", "io.swagger.annotations.ApiModelProperty");
        importMapping.remove("ApiModel", "io.swagger.annotations.ApiModel");
        setDateLibrary("java8");
        typeMapping.put("date", "Instant");
        importMapping.put("Instant", "java.time.Instant");
    }

    @Override
    public void processOpts() {
        super.processOpts();

        // mainClassName
        // additionalProperties.put("mainClassName", camelize(Objects.toString(additionalProperties.get("mainClassName").toString(), "default"), CamelizeOption.UPPERCASE_FIRST_CHAR));
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
        objs = ApiSpecUtil.preprocessOperations(objs);
        return objs;
    }
}
