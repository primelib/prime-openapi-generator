package io.github.primelib.primecodegen.core.domain.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.primelib.primecodegen.core.util.JacksonUtil;
import lombok.Getter;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class NitroGeneratorOperationData {

    private String packageName;
    private String classname;
    private Collection<NitroGeneratorImport> imports;
    private String importPath;

    private CodegenOperation codegenOperation;

    public static NitroGeneratorOperationData of(CodegenOperation data, CodegenConfig config) {
        NitroGeneratorOperationData response = new NitroGeneratorOperationData();

        response.packageName = config.modelPackage();
        response.classname = config.toModelName(data.operationId);
        response.imports = new ArrayList<>();
        response.codegenOperation = data;

        return response;
    }

    public static List<NitroGeneratorOperationData> ofList(Collection<CodegenOperation> data, CodegenConfig config) {
        return data.stream().map(d -> of(d, config)).collect(Collectors.toList());
    }

    @JsonIgnore
    public Map<String, Object> asMap() {
        return JacksonUtil.OBJECT_MAPPER.convertValue(this, new TypeReference<HashMap<String, Object>>() {});
    }
}
