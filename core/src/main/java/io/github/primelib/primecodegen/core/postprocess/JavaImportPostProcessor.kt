package io.github.primelib.primecodegen.core.postprocess

class JavaImportPostProcessor {

    companion object {
        private val importRegex = Regex("""^\s*import\s+.*?;\s*$""")
        private val packageRegex = Regex("""^\s*package\s+(.*?);\s*$""")

        /**
         * Removes unused imports from the given java code.
         */
        fun removeUnusedImports(javaCode: String): String {
            val imports = mutableListOf<String>()
            val lines = javaCode.lines().toMutableList()
            val currentPackage: String? = lines.firstOrNull { packageRegex.find(it) != null }?.let { packageRegex.find(it)?.groupValues?.get(1) }

            // find imports and remove them from lines
            val iterator = lines.iterator()
            while (iterator.hasNext()) {
                val line = iterator.next()
                if (line.matches(importRegex)) {
                    imports.add(line)
                    iterator.remove()
                }
            }

            // find unused imports
            val unusedImports = mutableListOf<String>()
            for (importLine in imports) {
                val className = importLine.substringAfterLast(".").trimEnd(';')
                if (className == "*") {
                    continue
                }

                val isUsed = lines.any { line -> Regex(Regex.escape(className)).containsMatchIn(line) }
                if (!isUsed) {
                    unusedImports.add(importLine)
                }
            }

            // find same package imports
            currentPackage?.let { pkg ->
                val packageImports = imports.filter { it.startsWith("import $currentPackage.") && it.lastIndexOf('.') == pkg.length + 7 }
                unusedImports.addAll(packageImports)
            }

            // find duplicate imports
            val duplicateImports = imports.groupBy { it }.filter { it.value.size > 1 }.map { it.key }
            unusedImports.addAll(duplicateImports)

            // remove unused imports
            var result = javaCode
            for (unusedImport in unusedImports) {
                val pattern = Regex(Regex.escape(unusedImport) + "\\R", RegexOption.MULTILINE)
                result = result.replaceFirst(pattern, "")
            }

            return result
        }
    }

}
