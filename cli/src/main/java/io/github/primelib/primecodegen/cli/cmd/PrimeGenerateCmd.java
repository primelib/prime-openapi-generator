package io.github.primelib.primecodegen.cli.cmd;

import io.airlift.airline.Command;
import io.github.primelib.primecodegen.cli.generator.NitroGenerator;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.Generator;
import org.openapitools.codegen.cmd.Generate;

import java.lang.reflect.Field;

@Command(name = "prime-generate", description = "Generate code with the specified generator.")
@Slf4j
public class PrimeGenerateCmd extends Generate {

    @Override
    public void execute() {
        replaceDefaultCodegen(new NitroGenerator(false));
        super.execute();
    }

    /**
     * replaces the private field for the openapi generator
     *
     * @param generator Generator
     */
    private void replaceDefaultCodegen(Generator generator) {
        try {
            Field field = Generate.class.getDeclaredField("generator");
            field.setAccessible(true);
            field.set(this, generator);
        } catch (Exception ex) {
            log.error("Failed to replace default codegen", ex);
            System.exit(1);
        }
    }
}
