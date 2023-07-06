package io.github.primelib.primecodegen.javafeign.config;

import com.github.twitch4j.codegen.core.api.INitroCodegenConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class JavaFeignGeneratorConfig implements INitroCodegenConfig {

    public static final String JETBRAINS_ANNOTATION_NULLABLE = "jetbrainsAnnotationsNullable";
    public static final String REQUEST_OVERLOAD_SPEC = "requestOverloadSpec";

    private Boolean hideGenerationTimestamp = true;
    private Boolean hideLicense = true;
    private Boolean serializableModel = false;
    private Boolean jetbrainsAnnotationsNullable = true;
    private Boolean requestOverloadSpec = false;

    public static JavaFeignGeneratorConfig of(Map<String, Object> data) {
        JavaFeignGeneratorConfig cfg = new JavaFeignGeneratorConfig();

        // defaults
        cfg.setHideGenerationTimestamp(true);
        cfg.setHideLicense(true);
        cfg.setSerializableModel(false);
        cfg.setJetbrainsAnnotationsNullable(true);
        cfg.setRequestOverloadSpec(true);

        // extract from data
        if (data != null) {
            cfg.setRequestOverloadSpec((Boolean) data.get(REQUEST_OVERLOAD_SPEC));
        }

        return cfg;
    }

}
