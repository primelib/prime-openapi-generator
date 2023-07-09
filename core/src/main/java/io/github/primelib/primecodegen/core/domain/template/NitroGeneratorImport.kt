package io.github.primelib.primecodegen.core.domain.template;

data class NitroGeneratorImport(
    val importPath: String
) {
    companion object {
        fun of(data: Map<String, Any>): NitroGeneratorImport {
            return NitroGeneratorImport(
                importPath = data["import"] as String
            )
        }

        fun ofList(data: Collection<Map<String, Any>>): MutableList<NitroGeneratorImport> {
            return data.map { of(it) }.toMutableList()
        }
    }
}
