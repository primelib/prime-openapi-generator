package io.github.primelib.primecodegen.javafeign;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;

/***
 * This test allows you to easily launch your code generation software under a debugger.
 * Then run this test under debug mode.  You will be able to step through your java code
 * and then see the results in the out directory.
 */
public class JavaFeignGeneratorTest {

    @Test
    @Disabled
    public void launchCodeGenerator() {
        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName(JavaFeignGenerator.CODEGEN_NAME) // use this codegen library
                .setInputSpec("openapi.yaml")
                .setPackageName("com.petstore")
                .setModelPackage("com.petstore.domain")
                .setApiPackage("com.petstore.api")
                .setArtifactId("petstore")
                .setTemplatingEngineName("auto")
                .setOutputDir("out/" + JavaFeignGenerator.CODEGEN_NAME);

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
        generator.setGenerateMetadata(false);
        generator.opts(clientOptInput).generate();
    }
}
