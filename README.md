# *PrimeCodeGen* - A custom openapi code generator

## The Generator

The official generator is not very flexible (e.g. it is not possible to generate a file for each api operation).

This custom generator introduces the following features:

* a different template engine (pebble), which is easier to read/maintain than mustache
* typed templates with ide-plugin for code-completion
* a very flexible template registration system

**Example**:

```kt
cfg.templateSpecs.add(PrimeTemplateSpec(
    description = "api interface for reactor",
    sourceTemplate = "api_main.peb",
    targetDirectory = apiFolder,
    targetFileName = "{mainClassName}ReactorApi.java",
    scope = TemplateScope.API,
    iterator = PrimeIterator.EACH_API,
    transform = { data ->
        data.mainClassName = "${data.mainClassName}Reactor"
    }
))
```

- iterator: `ONCE_API`, `EACH_API`, `EACH_API_OPERATION`, `ONCE_MODEL`, `EACH_MODEL`, `ONCE_PROJECT`
- filter: dynamic filter to decide if the template should be executed (e.g. only for api operations with parameters)
- transform: dynamically transform data before the template is executed

## Usage

TODO: add docker image / jar download link for cli usage

```
java -jar openapi-generator.jar generate -e auto -i openapi.yaml -o /my-project -c /my-project/openapi-generator.json
```

## Limitations

- this generator *MUST* be used from the CLI, it is not compatible with the gradle or maven plugins

## License

Released under the [MIT License](./LICENSE).
