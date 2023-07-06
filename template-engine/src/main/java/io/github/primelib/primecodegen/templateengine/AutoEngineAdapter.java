package io.github.primelib.primecodegen.templateengine;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.openapitools.codegen.api.TemplatingEngineAdapter;
import org.openapitools.codegen.api.TemplatingExecutor;
import org.openapitools.codegen.templating.HandlebarsEngineAdapter;
import org.openapitools.codegen.templating.MustacheEngineAdapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Slf4j
public class AutoEngineAdapter implements TemplatingEngineAdapter {

    private static final List<TemplatingEngineAdapter> engineAdapters = List.of(
        new MustacheEngineAdapter(),
        new HandlebarsEngineAdapter(),
        new PebbleEngineAdapter()
    );
    private static final List<String> extensions = engineAdapters.stream().map(TemplatingEngineAdapter::getFileExtensions).flatMap(Arrays::stream).collect(Collectors.toUnmodifiableList());

    @Override
    public String getIdentifier() {
        return "auto";
    }

    @Override
    public String[] getFileExtensions() {
        return extensions.toArray(new String[]{});
    }

    @Override
    public String compileTemplate(TemplatingExecutor executor, Map<String, Object> bundle, String templateFile) throws IOException {
        String extension = FilenameUtils.getExtension(templateFile);

        // select the correct engine adapter for this file extension
        for (TemplatingEngineAdapter engineAdapter : engineAdapters) {
            if (Arrays.stream(engineAdapter.getFileExtensions()).anyMatch(extension::equalsIgnoreCase)) {
                log.debug("Template {} will use engine {}", templateFile, engineAdapter.getIdentifier());
                return engineAdapter.compileTemplate(executor, bundle, templateFile);
            }
        }

        log.debug("No template engine found for file extension {} - File: {}", extension, templateFile);
        return null;
    }
}
