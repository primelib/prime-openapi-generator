package io.github.primelib.primecodegen.cli.util;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.InlineModelResolver;
import org.openapitools.codegen.model.ModelsMap;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@UtilityClass
@Slf4j
public class NitroUtils {

    public String removeTrailingSlash(@Nullable String value) {
        if (value == null) {
            return null;
        }

        return StringUtils.removeEnd(value, "/");
    }

    /**
     * Flatten OpenAPI Spec
     */
    public void flattenOpenAPISpec(@NotNull OpenAPI openAPI) {
        try {
            InlineModelResolver inlineModelResolver = new InlineModelResolver();
            Method method = InlineModelResolver.class.getDeclaredMethod("flatten", OpenAPI.class);
            method.setAccessible(true);
            method.invoke(inlineModelResolver, openAPI);
        } catch (Exception ex) {
            log.error("Failed to flatten openapi spec!", ex);
            System.exit(1);
        }
    }

    /**
     * Generate Files Metadata
     */
    public void generateFilesMetadata(DefaultGenerator generator, @NotNull List<File> files) {
        try {
            Method method = DefaultGenerator.class.getDeclaredMethod("generateFilesMetadata", List.class);
            method.setAccessible(true);
            method.invoke(generator, files);
        } catch (Exception ex) {
            log.error("Failed to flatten openapi spec!", ex);
            System.exit(1);
        }
    }

    /**
     * Generate Model Metadata
     */
    public ModelsMap processModels(DefaultGenerator generator, CodegenConfig config, Map<String, Schema> definitions) {
        try {
            Method method = DefaultGenerator.class.getDeclaredMethod("processModels", CodegenConfig.class, Map.class);
            method.setAccessible(true);
            return (ModelsMap) method.invoke(generator, config, definitions);
        } catch (Exception ex) {
            log.error("failed to process model data", ex);
        }

        return null;
    }

    /**
     * Generate Operation Metadata
     */
    public Map<String, Object> processOperations(DefaultGenerator generator, CodegenConfig config, String tag, List<CodegenOperation> ops, List<Object> models) {
        try {
            Method method = DefaultGenerator.class.getDeclaredMethod("processOperations", CodegenConfig.class, String.class, List.class, List.class);
            method.setAccessible(true);
            return (Map<String, Object>) method.invoke(generator, config, tag, ops, models);
        } catch (Exception ex) {
            log.error("failed to process model data", ex);
        }

        return null;
    }

    public void addAuthenticationSwitches(DefaultGenerator generator, Map<String, Object> bundle) {
        try {
            Method method = DefaultGenerator.class.getDeclaredMethod("addAuthenticationSwitches", Map.class);
            method.setAccessible(true);
            method.invoke(generator, bundle);
        } catch (Exception ex) {
            log.error("failed to process model data", ex);
        }
    }

}
