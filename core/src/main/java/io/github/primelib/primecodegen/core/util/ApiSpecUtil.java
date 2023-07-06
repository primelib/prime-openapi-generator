package io.github.primelib.primecodegen.core.util;

import lombok.experimental.UtilityClass;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.model.OperationsMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@UtilityClass
public class ApiSpecUtil {

    /**
     * Enrich the operations with additional vendor extensions
     *
     * @param objs OperationsMap
     * @return OperationsMap
     */
    @SuppressWarnings("unchecked")
    public OperationsMap preprocessOperations(OperationsMap objs) {
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

}
