package io.github.primelib.primecodegen.core.domain.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.primelib.primecodegen.core.util.JacksonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class NitroGeneratorModelData {

    private String packageName;
    private String classname;
    private Collection<NitroGeneratorImport> imports;
    private String importPath;

    private String title;
    private String description;
    private Boolean isEnum;

    private Boolean isDeprecated;
    private Map<String, Object> vendorExtensions;
    private CodegenModel codegenModel;

    public static NitroGeneratorModelData of(Map<String, Object> data, CodegenConfig config) {
        NitroGeneratorModelData response = new NitroGeneratorModelData();
        response.packageName = (String) data.get("package");
        response.classname = (String) data.get("classname");
        response.imports = NitroGeneratorImport.ofList((List<Map<String, Object>>) data.get("imports"));

        response.title = (String) data.get("title");
        response.description = (String) data.get("description");
        response.isEnum = (Boolean) data.get("isEnum");

        for (Map<String, Object> model : (List<Map<String, Object>>) data.get("models")) {
            response.importPath = (String) model.get("importPath");
            response.isDeprecated = (Boolean) data.get("isDeprecated");
            response.vendorExtensions = (Map<String, Object>) data.get("vendorExtensions");
            response.codegenModel = (CodegenModel) model.get("model");
        }

        return response;
    }

    public static List<NitroGeneratorModelData> ofList(Collection<Map<String, Object>> data, CodegenConfig config) {
        return data.stream().map(d -> of(d, config)).collect(Collectors.toList());
    }

    @JsonIgnore
    public Map<String, Object> asMap() {
        return JacksonUtil.OBJECT_MAPPER.convertValue(this, new TypeReference<HashMap<String, Object>>() {});
    }
}
