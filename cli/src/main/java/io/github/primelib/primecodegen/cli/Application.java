package io.github.primelib.primecodegen.cli;

import io.github.primelib.primecodegen.cli.cmd.NitroGenerateCmd;
import io.airlift.airline.Cli;
import io.airlift.airline.ParseArgumentsUnexpectedException;
import io.airlift.airline.ParseOptionMissingException;
import io.airlift.airline.ParseOptionMissingValueException;
import org.openapitools.codegen.OpenAPIGenerator;
import org.openapitools.codegen.cmd.AuthorTemplate;
import org.openapitools.codegen.cmd.BuildInfo;
import org.openapitools.codegen.cmd.CompletionCommand;
import org.openapitools.codegen.cmd.ConfigHelp;
import org.openapitools.codegen.cmd.GenerateBatch;
import org.openapitools.codegen.cmd.HelpCommand;
import org.openapitools.codegen.cmd.ListGenerators;
import org.openapitools.codegen.cmd.Meta;
import org.openapitools.codegen.cmd.OpenApiGeneratorCommand;
import org.openapitools.codegen.cmd.Validate;
import org.openapitools.codegen.cmd.Version;

import java.util.Locale;

import static org.openapitools.codegen.Constants.CLI_NAME;

public class Application extends OpenAPIGenerator {

    public static void main(String[] args) {
        BuildInfo buildInfo = new BuildInfo();
        Cli.CliBuilder<OpenApiGeneratorCommand> builder =
                Cli.<OpenApiGeneratorCommand>builder(CLI_NAME)
                        .withDescription(
                                String.format(
                                        Locale.ROOT,
                                        "OpenAPI Generator CLI %s (%s).",
                                        buildInfo.getVersion(),
                                        buildInfo.getSha()))
                        .withDefaultCommand(HelpCommand.class)
                        .withCommands(
                                ListGenerators.class,
                                NitroGenerateCmd.class,
                                Meta.class,
                                HelpCommand.class,
                                ConfigHelp.class,
                                Validate.class,
                                Version.class,
                                CompletionCommand.class,
                                GenerateBatch.class
                        );

        builder.withGroup("author")
                .withDescription("Utilities for authoring generators or customizing templates.")
                .withDefaultCommand(HelpCommand.class)
                .withCommands(AuthorTemplate.class);

        try {
            builder.build().parse(args).run();

            // If CLI runs without a command, consider this an error. This exists after initial parse/run
            // so we can present the configured "default command".
            // We can check against empty args because unrecognized arguments/commands result in an exception.
            // This is useful to exit with status 1, for example, so that misconfigured scripts fail fast.
            // We don't want the default command to exit internally with status 1 because when the default command is something like "list",
            // it would prevent scripting using the command directly. Example:
            //     java -jar cli.jar list --short | tr ',' '\n' | xargs -I{} echo "Doing something with {}"
            if (args.length == 0) {
                System.exit(1);
            }
        } catch (ParseArgumentsUnexpectedException e) {
            System.err.printf(Locale.ROOT, "[error] %s%n%nSee '%s help' for usage.%n", e.getMessage(), CLI_NAME);
            System.exit(1);
        } catch (ParseOptionMissingException | ParseOptionMissingValueException e) {
            System.err.printf(Locale.ROOT, "[error] %s%n", e.getMessage());
            System.exit(1);
        }
    }

}
