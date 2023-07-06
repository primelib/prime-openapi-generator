# Generator - Java Feign

Generates the Client using the Library Feign.

## Vendor Extensions

Vendor Extensions can be used to customize the generated code even further, this is a list of all supported vendor extensions supported by this generator.

### Deprecation

When marking a schema or operation as deprecated, the vendor extension `x-deprecated` can be used to provide library users with more information about the deprecation / alternatives.

Example:

```yaml
deprecated: true
x-deprecated: "Deprecated because abc. Use Z instead."
```

### Content Type

```yaml
x-contentType: "application/json"
```

### Accepts

```yaml
x-accepts: "application/json"
```
