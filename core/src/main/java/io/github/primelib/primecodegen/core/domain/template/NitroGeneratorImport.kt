package io.github.primelib.primecodegen.core.domain.template;

data class NitroGeneratorImport(
    val importpath: String
) {
    companion object {
        fun of(data: Map<String, Any>): NitroGeneratorImport {
            return NitroGeneratorImport(
                importpath = data["import"] as String
            )
        }

        fun ofList(data: Collection<Map<String, Any>>): List<NitroGeneratorImport> {
            return data.map { of(it) }
        }
    }
}
