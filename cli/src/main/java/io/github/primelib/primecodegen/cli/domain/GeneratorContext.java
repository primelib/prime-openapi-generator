package io.github.primelib.primecodegen.cli.domain;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Data;
import org.openapitools.codegen.CodegenOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class GeneratorContext {

    /**
     * all schemas
     */
    Map<String, Schema> schemaMap = null;

    /**
     * list of all model keys (should only be used before models is filled)
     */
    Set<String> modelKeys = new HashSet<>();

    /**
     * a map that holds the generated model template data for each template (by key)
     */
    Map<String, Map<String, Object>> models = new HashMap<>();

    /**
     * list of all api keys
     */
    Set<String> apiKeys = new HashSet<>();

    /**
     * holds all api's and processed data
     */
    Map<String, Map<String, Object>> apiMap = new HashMap<>();

    /**
     * operations
     */
    Map<String, CodegenOperation> operations = new HashMap<>();

    /**
     * operations per tag
     */
    Map<String, List<CodegenOperation>> apiOperations = new HashMap<>();

    /**
     * all generated files
     */
    List<File> files = new ArrayList<>();
}
