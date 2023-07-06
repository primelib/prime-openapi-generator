package com.github.twitch4j.codegen.core.domain.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.twitch4j.codegen.core.api.INitroCodegenConfig;
import com.github.twitch4j.codegen.core.util.JacksonUtil;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * base generator data
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NitroGeneratorData {

    // generator config
    private Boolean dryRun = false;
    private OpenAPI openAPI;
    private Boolean debugOpenAPI = false;
    private Boolean generateApis = true;
    private Boolean generateApiTests = true;
    private Boolean generateApiDocumentation = true;
    private Boolean generateModels = true;
    private Boolean generateModelTests = true;
    private Boolean generateModelDocumentation = true;
    private Boolean generateSupportingFiles = true;
    private Boolean generateSkipFormModel = false;
    private Boolean generateMetadata = true;

    // template config
    private INitroCodegenConfig config;

    // generator info
    private String generatorVersion;
    private String generatorDate;
    private String generatorYear;
    private String generatorClass;
    private String inputSpec;

    // server
    private String contextPath;
    private String basePathWithoutHost;
    private String basePath;

    // packages
    private String apiPackage;
    private String modelPackage;
    private String testPackage;
    private String mainClassName;
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * OpenAPI Info - Name
     */
    private String appName;

    /**
     * OpenAPI Info - Version
     */
    private String appVersion;

    /**
     * OpenAPI Info - Description
     */
    private String appDescription;

    private String infoEmail;

    private String infoName;

    private String infoUrl;

    private String licenseInfo;

    private String licenseUrl;

    private String licenseName;

    private String termsOfService;

    private NitroGeneratorModelData model;

    private List<NitroGeneratorModelData> models = new ArrayList<>();

    private NitroGeneratorOperationData operation;

    private List<NitroGeneratorOperationData> operations = new ArrayList<>();

    private NitroGeneratorApiData api;

    private List<NitroGeneratorApiData> apis = new ArrayList<>();

    @JsonIgnore
    public Map<String, Object> asMap() {
        return JacksonUtil.OBJECT_MAPPER.convertValue(this, new TypeReference<HashMap<String, Object>>() {});
    }
}
