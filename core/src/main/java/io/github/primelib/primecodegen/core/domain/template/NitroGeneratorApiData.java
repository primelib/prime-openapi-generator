package io.github.primelib.primecodegen.core.domain.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.primelib.primecodegen.core.util.JacksonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenOperation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class NitroGeneratorApiData {

    private String packageName;
    private String classname;
    private Collection<NitroGeneratorImport> imports;

    private List<CodegenOperation> operations;

    public static NitroGeneratorApiData of(Map<String, Object> api, CodegenConfig config) {
        Map<String, Object> apiData = (Map<String, Object>) api.get("operations");

        NitroGeneratorApiData response = new NitroGeneratorApiData();
        response.packageName = (String) api.get("package");
        response.classname = (String) apiData.get("classname");
        response.imports = NitroGeneratorImport.ofList((List<Map<String, Object>>) api.get("imports"));
        response.operations = (List<CodegenOperation>) apiData.get("operation");

        return response;
    }

    public static List<NitroGeneratorApiData> ofList(Collection<Map<String, Object>> data, CodegenConfig config) {
        return data.stream().map(d -> of(d, config)).collect(Collectors.toList());
    }

    @JsonIgnore
    public Map<String, Object> asMap() {
        return JacksonUtil.OBJECT_MAPPER.convertValue(this, new TypeReference<HashMap<String, Object>>() {});
    }
}
