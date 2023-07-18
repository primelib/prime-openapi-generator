package io.github.primelib.primecodegen.cli.generator;

import io.github.primelib.primecodegen.cli.domain.GeneratorContext;
import io.github.primelib.primecodegen.cli.util.NitroUtils;
import io.github.primelib.primecodegen.core.api.PrimeCodegenBase;
import io.github.primelib.primecodegen.core.domain.config.PrimeTemplateSpec;
import io.github.primelib.primecodegen.core.domain.config.TemplateIterator;
import io.github.primelib.primecodegen.core.domain.config.TemplateScope;
import io.github.primelib.primecodegen.core.domain.template.AuthTemplateData;
import io.github.primelib.primecodegen.core.domain.template.NitroGeneratorApiData;
import io.github.primelib.primecodegen.core.domain.template.NitroGeneratorData;
import io.github.primelib.primecodegen.core.domain.template.NitroGeneratorModelData;
import io.github.primelib.primecodegen.core.domain.template.NitroGeneratorOperationData;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.Generator;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.meta.GeneratorMetadata;
import org.openapitools.codegen.meta.Stability;
import org.openapitools.codegen.model.ModelsMap;
import org.openapitools.codegen.serializer.SerializerUtils;
import org.openapitools.codegen.utils.CamelizeOption;
import org.openapitools.codegen.utils.ImplementationVersion;
import org.openapitools.codegen.utils.ModelUtils;
import org.openapitools.codegen.utils.URLPathUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.openapitools.codegen.utils.StringUtils.camelize;

/**
 * NitroGenerator
 */
@Slf4j
public class NitroGenerator extends DefaultGenerator implements Generator {
    protected CodegenConfig config;
    protected ClientOptInput opts;
    protected OpenAPI openAPI;
    protected NitroGeneratorData nitroGeneratorData = new NitroGeneratorData();
    protected Map<String, String> generatorPropertyDefaults = new HashMap<>();

    public NitroGenerator() {
        this(false);
    }

    public NitroGenerator(Boolean dryRun) {
        super(dryRun);
        nitroGeneratorData.setDryRun(dryRun);

        log.info("Generating with PrimeGenerator. dryRun={}", dryRun);
    }

    public Generator opts(ClientOptInput opts) {
        super.opts(opts);

        this.opts = opts;
        this.openAPI = opts.getOpenAPI();
        this.config = opts.getConfig();

        return this;
    }

    public void setGeneratorPropertyDefault(String key, String value) {
        // intercept defaults and store them
        super.setGeneratorPropertyDefault(key, value);
        this.generatorPropertyDefaults.put(key, value);
    }

    /**
     * puts all template vars into a new var t
     */
    @Override
    protected File processTemplateToFile(Map<String, Object> templateData, String templateName, String outputFilename, boolean shouldGenerate, String skippedByOption) throws IOException {
        Map<String, Object> finalTemplateData = new HashMap<>();
        if (templateName.endsWith(".peb")) {
            if (templateData != null) {
                finalTemplateData.put("t", templateData);
                finalTemplateData.put("cfg", templateData.get("config"));
            } else {
                log.error("can't generate template {} -> {}, no template data available!", templateName, outputFilename);
                return null;
            }
        }

        return super.processTemplateToFile(finalTemplateData, templateName, outputFilename, shouldGenerate, skippedByOption);
    }

    public List<File> generate() {
        List<File> files = new LinkedList<>();

        if (this.openAPI == null) {
            throw new RuntimeException("Issues with the OpenAPI input. Possible causes: invalid/missing spec, malformed JSON/YAML files, etc.");
        } else if (this.config == null) {
            throw new RuntimeException("missing config!");
        }

        if (this.config.getGeneratorMetadata() == null) {
            log.warn("Generator '{}' is missing generator metadata!", this.config.getName());
        } else {
            GeneratorMetadata generatorMetadata = this.config.getGeneratorMetadata();
            if (StringUtils.isNotEmpty(generatorMetadata.getGenerationMessage())) {
                log.info(generatorMetadata.getGenerationMessage());
            }

            Stability stability = generatorMetadata.getStability();
            String stabilityMessage = String.format(Locale.ROOT, "Generator '%s' is considered %s.", this.config.getName(), stability.value());
            if (stability == Stability.DEPRECATED) {
                log.warn(stabilityMessage);
            } else {
                log.info(stabilityMessage);
            }
        }

        if (PrimeCodegenBase.class.isAssignableFrom(config.getClass())) {
            // nitro generator
            files.addAll(this.generateNitro());
        } else {
            // original generator
            files.addAll(super.generate());
        }

        return files;
    }

    /**
     * run custom generation
     */
    public synchronized List<File> generateNitro() {
        // init
        PrimeCodegenBase codegen = (PrimeCodegenBase) config;
        List<PrimeTemplateSpec> files = codegen.cfg().getTemplateSpecs();
        files.sort(Comparator.comparing(a -> a.getScope().ordinal()));
        nitroGeneratorData.setConfig(codegen.cfg());

        // error prevention
        if (config.apiTemplateFiles().size() > 0) {
            log.error("apiTemplateFiles only work when using the default generator");
        }

        // preprocess
        this.config.processOpts();
        this.config.preprocessOpenAPI(this.openAPI);
        this.config.setOpenAPI(this.openAPI);
        if (this.openAPI.getExtensions() != null) {
            this.config.vendorExtensions().putAll(this.openAPI.getExtensions());
        }

        // process
        NitroUtils.flattenOpenAPISpec(openAPI);
        configureGeneratorProperties();
        configureOpenAPIInfo();
        config.processOpenAPI(this.openAPI);
        // this.processUserDefinedTemplates(); // TODO: not supported yet

        // global template data
        nitroGeneratorData.setDetails(OpenAPIDetails.Companion.of(openAPI));
        nitroGeneratorData.setProject(ProjectData.Companion.of(config));
        nitroGeneratorData.setAuth(AuthTemplateData.Companion.of(openAPI));

        // context
        GeneratorContext ctx = new GeneratorContext();

        // prepare all template data
        prepareModelTemplateData(ctx);
        prepareApiTemplateData(ctx);

        // file generation
        log.info("found {} files for the NitroGenerator!", files.size());
        files.stream().parallel().forEach(f -> generateNitroFile(ctx, f));

        // metadata
        if (nitroGeneratorData.getGenerateMetadata()) {
            NitroUtils.generateFilesMetadata(this, ctx.getFiles());
            // TODO: generateFilesMetadata(ctx.getFiles());
        }

        // post-process
        this.config.postProcess();
        GlobalSettings.reset();

        if (nitroGeneratorData.getDebugOpenAPI()) {
            Json.prettyPrint(this.openAPI);
        }
        if (GlobalSettings.getProperty("debugModels") != null) {
            log.info("############ Model info ############");
            Json.prettyPrint(ctx.getModels());
        }
        if (GlobalSettings.getProperty("debugOperations") != null) {
            this.LOGGER.info("############ Operation info ############");
            Json.prettyPrint(ctx.getOperations());
        }

        return new ArrayList<>();
    }

    public void generateNitroFile(GeneratorContext ctx, PrimeTemplateSpec file) {
        log.info("generating file {} [Scope:{}]", file.getSourceTemplate(), file.getScope());

        try {
            if (TemplateScope.MODEL.equals(file.getScope())) {
                generateNitroFileModel(ctx, file, !nitroGeneratorData.getGenerateModels());
            } else if (TemplateScope.MODEL_TEST.equals(file.getScope())) {
                generateNitroFileModel(ctx, file, !nitroGeneratorData.getGenerateModelTests());
            } else if (TemplateScope.API.equals(file.getScope())) {
                generateNitroFileApi(ctx, file, !nitroGeneratorData.getGenerateApis());
            } else if (TemplateScope.API_TEST.equals(file.getScope())) {
                generateNitroFileApi(ctx, file, !nitroGeneratorData.getGenerateApiTests());
            } else if (TemplateScope.SUPPORT.equals(file.getScope())) {
                generateSupportingFile(ctx, file, !nitroGeneratorData.getGenerateSupportingFiles());
            } else {
                log.error("failed to generate file {}, not supported! [Scope:{}]", file.getSourceTemplate(), file.getScope());
            }
        } catch (IOException ex) {
            log.error("error while generating file {} [Scope:{}]", file.getSourceTemplate(), file.getScope(), ex);
        }
    }

    protected void generateNitroFileModel(GeneratorContext ctx, PrimeTemplateSpec file, Boolean skipped) throws IOException {
        if (skipped) return;

        if (TemplateIterator.ONCE_MODEL.equals(file.getIterator())) {
        } else if (TemplateIterator.EACH_MODEL.equals(file.getIterator())) {
            ctx.getModels().keySet().stream().parallel().forEach(name -> {
                try {
                    NitroGeneratorData templateData = nitroGeneratorData.getCopy();
                    templateData.setModel(NitroGeneratorModelData.Companion.of(ctx.getModels().get(name), this.config));
                    templateData.setModels(NitroGeneratorModelData.Companion.ofList(ctx.getModels().values(), this.config));

                    // dynamic filter and template transformation
                    if (!file.getFilter().invoke(templateData)) {
                        return;
                    }
                    file.getTransform().invoke(templateData);

                    processFile(ctx, file, name, templateData.asMap(), file.getSkippedBy());
                } catch (Exception ex) {
                    log.warn("failed to generate file {} [Template: {}]", file.getTargetFileName(), file.getSourceTemplate(), ex);
                }
            });
        } else if (TemplateIterator.EACH_API_OPERATION.equals(file.getIterator())) {
            ctx.getApiKeys().stream().parallel().forEach(name -> {
                List<NitroGeneratorOperationData> operations = NitroGeneratorOperationData.Companion.ofList(ctx.getApiOperations().get(name), this.config);
                operations.stream().parallel().forEach(operation -> {
                    try {
                        NitroGeneratorData templateData = nitroGeneratorData.getCopy();
                        templateData.setModels(NitroGeneratorModelData.Companion.ofList(ctx.getModels().values(), this.config));
                        templateData.setOperation(operation);
                        templateData.setOperations(operations);
                        templateData.setApi(NitroGeneratorApiData.Companion.of(ctx.getApiMap().get(name), this.config));

                        // dynamic filter and template transformation
                        if (!file.getFilter().invoke(templateData)) {
                            return;
                        }
                        file.getTransform().invoke(templateData);

                        processFile(ctx, file, operation.getClassname(), templateData.asMap(), file.getSkippedBy());
                    } catch (Exception ex) {
                        log.warn("failed to generate file {} [Template: {}]", file.getTargetFileName(), file.getSourceTemplate(), ex);
                    }
                });
            });
        } else {
            log.warn("Iterator {} is not supported for {}", file.getIterator(), file.getScope());
        }
    }

    protected void generateNitroFileApi(GeneratorContext ctx, PrimeTemplateSpec file, Boolean skipped) throws IOException {
        if (skipped) return;

        if (TemplateIterator.ONCE_API.equals(file.getIterator())) {
            try {
                NitroGeneratorData templateData = nitroGeneratorData.getCopy();
                templateData.setModels(NitroGeneratorModelData.Companion.ofList(ctx.getModels().values(), this.config));
                templateData.setOperations(NitroGeneratorOperationData.Companion.ofList(ctx.getApiOperations().values().stream().flatMap(Collection::stream).collect(Collectors.toList()), this.config));
                templateData.setApis(NitroGeneratorApiData.Companion.ofList(ctx.getApiMap().values(), this.config));

                // dynamic filter and template transformation
                if (!file.getFilter().invoke(templateData)) {
                    return;
                }
                file.getTransform().invoke(templateData);

                processFile(ctx, file, file.getTargetFileName(), templateData.asMap(), file.getSkippedBy());
            } catch (Exception ex) {
                log.warn("failed to generate file {} [Template: {}]", file.getTargetFileName(), file.getSourceTemplate(), ex);
            }

            // nitroGeneratorData.setModels(NitroGeneratorModelData.ofList(ctx.getModels().values(), this.config));
            // nitroGeneratorData.setOperations(NitroGeneratorOperationData.ofList(ctx.getOperations().values(), this.config));
            // nitroGeneratorData.setApi(NitroGeneratorApiData.of(ctx.getApiOperations(), this.config));
        } else if (TemplateIterator.EACH_API.equals(file.getIterator())) {
            ctx.getApiKeys().stream().parallel().forEach(name -> {
                try {
                    NitroGeneratorData templateData = nitroGeneratorData.getCopy();
                    templateData.setModels(NitroGeneratorModelData.Companion.ofList(ctx.getModels().values(), this.config));
                    templateData.setOperations(NitroGeneratorOperationData.Companion.ofList(ctx.getApiOperations().get(name), this.config));
                    templateData.setApi(NitroGeneratorApiData.Companion.of(ctx.getApiMap().get(name), this.config));

                    // dynamic filter and template transformation
                    if (!file.getFilter().invoke(templateData)) {
                        return;
                    }
                    file.getTransform().invoke(templateData);

                    processFile(ctx, file, name, templateData.asMap(), file.getSkippedBy());
                } catch (Exception ex) {
                    log.warn("failed to generate file {} [Template: {}]", file.getTargetFileName(), file.getSourceTemplate(), ex);
                }
            });
        } else {
            log.warn("Iterator {} is not supported for {}", file.getIterator(), file.getScope());
        }
    }

    protected void generateSupportingFile(GeneratorContext ctx, PrimeTemplateSpec file, Boolean skipped) throws IOException {
        if (skipped) return;

        try {
            NitroGeneratorData templateData = nitroGeneratorData.getCopy();
            templateData.setModels(NitroGeneratorModelData.Companion.ofList(ctx.getModels().values(), this.config));

            // dynamic filter and template transformation
            if (!file.getFilter().invoke(templateData)) {
                return;
            }
            file.getTransform().invoke(templateData);

            processFile(ctx, file, file.getTargetFileName(), templateData.asMap(), file.getSkippedBy());
        } catch (Exception ex) {
            log.warn("failed to generate file {} [Template: {}]", file.getTargetFileName(), file.getSourceTemplate(), ex);
        }
    }

    /**
     * processes a file and writes it to disk
     *
     * @param ctx GeneratorContext
     * @param file NitroCodegenFile
     * @param name name
     * @param templateData templateData
     * @param skippedBy the property that needs to be set to skip generation of this file
     * @throws IOException
     */
    private void processFile(@NotNull GeneratorContext ctx, @NotNull PrimeTemplateSpec file, @NotNull String name, @NotNull Map<String, Object> templateData, @NotNull String skippedBy) throws IOException {
        String filename = file.getTargetDirectory() + File.separator + this.getFilename(file, name);

        if (StringUtils.isNotEmpty(file.getSourceTemplate())) {
            log.debug("generating file {} from template {}", filename, file.getSourceTemplate());

            File written = this.processTemplateToFile(templateData, file.getSourceTemplate(), filename, nitroGeneratorData.getGenerateModels(), skippedBy);
            if (written != null) {
                if (this.config.isEnablePostProcessFile() && !nitroGeneratorData.getDryRun()) {
                    this.config.postProcessFile(written, file.getScope().getPostProcessType());
                }

                ctx.getFiles().add(written);
            }
        } else {
            throw new RuntimeException("No template or source file specified for " + file.getTargetFileName());
        }
    }

    /**
     * prepare template data - global / generator
     */
    protected void configureGeneratorProperties() {
        // generator config
        nitroGeneratorData.setOpenAPI(openAPI);
        nitroGeneratorData.setGenerateApis(GlobalSettings.getProperty("apis") != null ? Boolean.TRUE : Boolean.parseBoolean(this.generatorPropertyDefaults.getOrDefault("apis", "true")));
        nitroGeneratorData.setGenerateApiTests(GlobalSettings.getProperty("apiTests") != null ? Boolean.TRUE : Boolean.parseBoolean(this.generatorPropertyDefaults.getOrDefault("apiTests", "true")));
        nitroGeneratorData.setGenerateApiDocumentation(GlobalSettings.getProperty("apiDocs") != null ? Boolean.TRUE : Boolean.parseBoolean(this.generatorPropertyDefaults.getOrDefault("apiDocs", "true")));
        nitroGeneratorData.setGenerateModels(GlobalSettings.getProperty("models") != null ? Boolean.TRUE : Boolean.parseBoolean(this.generatorPropertyDefaults.getOrDefault("models", "true")));
        nitroGeneratorData.setGenerateModelTests(GlobalSettings.getProperty("modelTests") != null ? Boolean.TRUE : Boolean.parseBoolean(this.generatorPropertyDefaults.getOrDefault("modelTests", "true")));
        nitroGeneratorData.setGenerateModelDocumentation(GlobalSettings.getProperty("modelDocs") != null ? Boolean.TRUE : Boolean.parseBoolean(this.generatorPropertyDefaults.getOrDefault("modelDocs", "true")));
        nitroGeneratorData.setGenerateSupportingFiles(GlobalSettings.getProperty("supportingFiles") != null ? Boolean.TRUE : Boolean.parseBoolean(this.generatorPropertyDefaults.getOrDefault("supportingFiles", "true")));
        nitroGeneratorData.setGenerateSkipFormModel(GlobalSettings.getProperty("skipFormModel") != null ? Boolean.TRUE : Boolean.parseBoolean(this.generatorPropertyDefaults.getOrDefault("skipFormModel", "false")));
        nitroGeneratorData.setDebugOpenAPI(GlobalSettings.getProperty("debugOpenAPI") != null);

        // generator version
        nitroGeneratorData.setGeneratorVersion(ImplementationVersion.read());
        nitroGeneratorData.setGeneratorDate(ZonedDateTime.now().toString());
        nitroGeneratorData.setGeneratorYear(String.valueOf(ZonedDateTime.now().getYear()));
        nitroGeneratorData.setGeneratorClass(this.config.getClass().getName());
        nitroGeneratorData.setInputSpec(this.config.getInputSpec());

        // packages
        nitroGeneratorData.setApiPackage(this.config.apiPackage());
        nitroGeneratorData.setModelPackage(this.config.modelPackage());
        nitroGeneratorData.setTestPackage(this.config.testPackage());
        if (openAPI.getInfo() != null) {
            nitroGeneratorData.setMainClassName(camelize(openAPI.getInfo().getTitle(), CamelizeOption.UPPERCASE_FIRST_CHAR).replace(" ", ""));
        } else {
            nitroGeneratorData.setMainClassName(camelize("default", CamelizeOption.UPPERCASE_FIRST_CHAR));
        }
        this.config.additionalProperties().put("mainClassName", nitroGeneratorData.getMainClassName());
        nitroGeneratorData.setAdditionalProperties(this.config.additionalProperties());

        // debugging
        if (GlobalSettings.getProperty("debugOpenAPI") != null) {
            System.out.println(SerializerUtils.toJsonString(this.openAPI));
        } else if (GlobalSettings.getProperty("debugSwagger") != null) {
            log.info("Please use system property 'debugOpenAPI' instead of 'debugSwagger'.");
            System.out.println(SerializerUtils.toJsonString(this.openAPI));
        }
    }

    /**
     * prepare template data - openapi
     */
    private void configureOpenAPIInfo() {
        // server setting
        URL url = URLPathUtils.getServerURL(this.openAPI, this.config.serverVariableOverrides());
        nitroGeneratorData.setContextPath(NitroUtils.removeTrailingSlash(this.config.escapeText(url.getPath())));
        nitroGeneratorData.setBasePathWithoutHost(nitroGeneratorData.getContextPath());
        nitroGeneratorData.setBasePath(NitroUtils.removeTrailingSlash(this.config.escapeText(URLPathUtils.getHost(this.openAPI, this.config.serverVariableOverrides()))));
    }

    /**
     * replaces all placeholders in the filename
     */
    @NotNull
    protected String getFilename(PrimeTemplateSpec file, String name) {
        String fileName = file.getTargetFileName();

        if (TemplateScope.MODEL.equals(file.getScope())) {
            fileName = fileName.replace("{name}", this.config.toModelFilename(name));
        } else if (TemplateScope.MODEL_TEST.equals(file.getScope())) {
            fileName = fileName.replace("{name}", this.config.toModelTestFilename(name));
        } else if (TemplateScope.MODEL_DOCS.equals(file.getScope())) {
            fileName = fileName.replace("{name}", this.config.toModelDocFilename(name));
        } else if (TemplateScope.API.equals(file.getScope())) {
            fileName = fileName.replace("{name}", this.config.toApiFilename(name));
        } else if (TemplateScope.API_TEST.equals(file.getScope())) {
            fileName = fileName.replace("{name}", this.config.toApiTestFilename(name));
        } else if (TemplateScope.API_DOCS.equals(file.getScope())) {
            fileName = fileName.replace("{name}", this.config.toApiDocFilename(name));
        }
        fileName = fileName.replace("{mainClassName}", nitroGeneratorData.getMainClassName());

        return fileName;
    }

    protected void prepareModelTemplateData(GeneratorContext ctx) {
        // get schemas
        Map<String, Schema> schemas = ModelUtils.getSchemas(this.openAPI);
        if (schemas == null) {
            log.warn("Skipping generation of models because specification document has no schemas.");
            return;
        }
        ctx.setSchemaMap(schemas);

        // which models should be generated?
        String modelNames = GlobalSettings.getProperty("models");
        Set<String> modelsToGenerate = modelNames != null && !modelNames.isEmpty() ? new HashSet<>(Arrays.asList(modelNames.split(","))) : null;

        // unused models
        List<String> unusedModels = ModelUtils.getSchemasUsedOnlyInFormParam(this.openAPI);

        // get a list of all models that should be generated
        Set<String> modelKeys = new HashSet<>(schemas.keySet());
        log.debug("the following models have been found in the spec: {}", modelKeys);

        // filter to specific models, if the target model names have been provided
        Optional.ofNullable(modelsToGenerate).ifPresent(toGenerate -> {
            modelKeys.removeIf(key -> !modelsToGenerate.contains(key));
        });
        ctx.setModelKeys(modelKeys);

        Map<String, ModelsMap> allProcessedModels = new TreeMap((o1, o2) -> ObjectUtils.compare(this.config.toModelName((String) o1), this.config.toModelName((String) o2)));
        Boolean skipFormModel = nitroGeneratorData.getGenerateSkipFormModel();
        modelKeys.forEach(name -> {
            try {
                if (this.config.importMapping().containsKey(name)) {
                    log.debug("Model {} not imported due to import mapping", name);
                    Iterator var22 = this.config.modelTemplateFiles().keySet().iterator();

                    while(var22.hasNext()) {
                        String templateName = (String)var22.next();
                        String filename = this.config.modelFilename(templateName, name);
                        Path path = Paths.get(filename);
                        this.templateProcessor.skip(path, "Skipped prior to model processing due to import mapping conflict (either by user or by generator).");
                    }
                } else {
                    if (unusedModels.contains(name)) {
                        if (!Boolean.FALSE.equals(skipFormModel)) {
                            log.info("Model {} not generated since it's marked as unused (due to form parameters) and `skipFormModel` (global property) set to true (default)", name);
                            return;
                        }

                        log.info("Model {} (marked as unused due to form parameters) is generated due to the global property `skipFormModel` set to false", name);
                    }

                    Schema schema = schemas.get(name);
                    if (ModelUtils.isFreeFormObject(this.openAPI, schema)) {
                        Schema refSchema = new Schema();
                        refSchema.set$ref("#/components/schemas/" + name);
                        Schema unaliasedSchema = this.config.unaliasSchema(refSchema);
                        if (unaliasedSchema.get$ref() == null) {
                            log.info("Model {} not generated since it's a free-form object", name);
                            return;
                        }
                    } else if (ModelUtils.isMapSchema(schema)) {
                        if (!ModelUtils.isGenerateAliasAsModel(schema) && !ModelUtils.isComposedSchema(schema) && (schema.getProperties() == null || schema.getProperties().isEmpty())) {
                            log.info("Model {} not generated since it's an alias to map (without property) and `generateAliasAsModel` is set to false (default)", name);
                            return;
                        }
                    } else if (ModelUtils.isArraySchema(schema) && !ModelUtils.isGenerateAliasAsModel(schema) && (schema.getProperties() == null || schema.getProperties().isEmpty())) {
                        log.info("Model {} not generated since it's an alias to array (without property) and `generateAliasAsModel` is set to false (default)", name);
                        return;
                    }

                    Map<String, Schema> schemaMap = new HashMap();
                    schemaMap.put(name, schema);
                    ModelsMap modelTemplate = NitroUtils.processModels(this, this.config, schemaMap);
                    // TODO: Map modelTemplate = processModels(this.config, schemaMap);
                    modelTemplate.put("classname", this.config.toModelName(name));
                    modelTemplate.putAll(this.config.additionalProperties());
                    allProcessedModels.put(name, modelTemplate);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Could not process model '" + name + "'.Please make sure that your schema is correct!", ex);
            }
        });

        // run update / post process on the final data
        Map<String, ModelsMap> processedModels = this.config.updateAllModels(allProcessedModels);
        processedModels = this.config.postProcessAllModels(processedModels);
        Map<String, Map<String, Object>> modelData = processedModels.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (Map<String, Object>) e.getValue()));
        ctx.setModels(modelData);
    }

    protected void prepareApiTemplateData(GeneratorContext ctx) {
        // get operations
        Map<String, List<CodegenOperation>> paths = this.processPaths(this.openAPI.getPaths());
        if (paths == null) {
            log.warn("Skipping generation of models because specification document has no schemas.");
            return;
        }

        // which models should be generated?
        String apiNames = GlobalSettings.getProperty("apis");
        Set<String> apisToGenerate = apiNames != null && !apiNames.isEmpty() ? new HashSet(Arrays.asList(apiNames.split(","))) : null;
        // todo: ...

        // api's
        ctx.getApiKeys().addAll(paths.keySet());
        ctx.getApiKeys().forEach(k -> ctx.getApiOperations().put(k, new ArrayList<>()));

        // for each operation
        ctx.getApiKeys().forEach(tag -> {
            try {
                List<CodegenOperation> operations = paths.get(tag);
                operations.sort((one, another) -> ObjectUtils.compare(one.operationId, another.operationId));
                Map<String, Object> operation = NitroUtils.processOperations(this, this.config, tag, operations, Arrays.asList(ctx.getModels().values().toArray()));
                // TODO: Map<String, Object> operation = processOperations(this.config, tag, operations, Arrays.asList(ctx.getModels().values().toArray()));

                operations.forEach(op -> {
                    log.info("processing operation " + op.operationId);

                    ctx.getOperations().put(op.operationId, op);
                    ctx.getApiOperations().get(tag).add(op);
                });

                if (!this.config.vendorExtensions().isEmpty()) {
                    operation.put("vendorExtensions", this.config.vendorExtensions());
                }

                boolean isGroupParameters;
                if (this.config.vendorExtensions().containsKey("x-group-parameters")) {
                    isGroupParameters = Boolean.parseBoolean(this.config.vendorExtensions().get("x-group-parameters").toString());
                    Map<String, Object> objectMap = (Map)operation.get("operations");
                    List<CodegenOperation> operationss = (List)objectMap.get("operation");
                    Iterator var15 = operationss.iterator();

                    while(var15.hasNext()) {
                        CodegenOperation op = (CodegenOperation)var15.next();
                        if (isGroupParameters && !op.vendorExtensions.containsKey("x-group-parameters")) {
                            op.vendorExtensions.put("x-group-parameters", Boolean.TRUE);
                        }
                    }
                }

                isGroupParameters = true;
                if (this.config.additionalProperties().containsKey("sortParamsByRequiredFlag")) {
                    isGroupParameters = Boolean.parseBoolean(this.config.additionalProperties().get("sortParamsByRequiredFlag").toString());
                }
                operation.put("sortParamsByRequiredFlag", isGroupParameters);
                ctx.getApiMap().put(tag, operation);
            } catch (Exception var18) {
                throw new RuntimeException("Could not generate api file for '" + tag + "'", var18);
            }
        });
    }
}
