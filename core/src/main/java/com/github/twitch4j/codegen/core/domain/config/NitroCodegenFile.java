package com.github.twitch4j.codegen.core.domain.config;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString
@Builder
public class NitroCodegenFile {

    private String sourceTemplate;

    private String targetDirectory;

    @NotNull
    private String targetFileName;

    /**
     * target scope / type of the generated files
     */
    private NitroScope scope;

    @Builder.Default
    private NitroIterator iterator = NitroIterator.ONCE_API;

    @Builder.Default
    private Boolean test = false;

    @Builder.Default
    private Boolean overwrite = false;

    public String getPostProcessType() {
        if (scope == NitroScope.MODEL) {
            return "model";
        } else if (scope == NitroScope.MODEL_TEST) {
            return "model-test";
        } else if (scope == NitroScope.MODEL_DOCS) {
            return "model-doc";
        } else if (scope == NitroScope.API) {
            return "api";
        } else if (scope == NitroScope.API_TEST) {
            return "api-test";
        } else if (scope == NitroScope.API_DOCS) {
            return "api-doc";
        }

        return "";
    }

    public String getSkippedBy() {
        if (scope == NitroScope.MODEL) {
            return "models";
        } else if (scope == NitroScope.API) {
            return "apis";
        }

        return "";
    }
}
