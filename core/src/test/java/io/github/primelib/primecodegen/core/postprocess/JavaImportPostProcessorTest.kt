package io.github.primelib.primecodegen.core.postprocess

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JavaImportPostProcessorTest {

    @Test
    fun testRemoveUnusedImports() {
        val javaCode = """
            package com.example;

            import java.util.List;
            import java.util.ArrayList;
            import java.util.Map;
            import java.util.HashMap;

            public class MyClass {
                public static void main(String[] args) {
                    List<String> myList = new ArrayList<>();
                }
            }
        """.trimIndent()

        val modifiedCode = JavaImportPostProcessor.removeUnusedImports(javaCode)
        val expectedCode = """
            package com.example;

            import java.util.List;
            import java.util.ArrayList;

            public class MyClass {
                public static void main(String[] args) {
                    List<String> myList = new ArrayList<>();
                }
            }
        """.trimIndent()

        assertEquals(expectedCode, modifiedCode)
    }

    @Test
    fun testRemoveDuplicateImports() {
        val javaCode = """
            package com.example;

            import java.util.List;
            import java.util.ArrayList;
            import java.util.ArrayList;

            public class MyClass {
                public static void main(String[] args) {
                    List<String> myList = new ArrayList<>();
                }
            }
        """.trimIndent()

        val modifiedCode = JavaImportPostProcessor.removeUnusedImports(javaCode)
        val expectedCode = """
            package com.example;

            import java.util.List;
            import java.util.ArrayList;

            public class MyClass {
                public static void main(String[] args) {
                    List<String> myList = new ArrayList<>();
                }
            }
        """.trimIndent()

        assertEquals(expectedCode, modifiedCode)
    }

    @Test
    fun testRemoveSamePackageImports() {
        val javaCode = """
            package com.example;

            import java.util.List;
            import com.example.ArrayList;

            public class MyClass {
                public static void main(String[] args) {
                    List<String> myList = new ArrayList<>();
                }
            }
        """.trimIndent()

        val modifiedCode = JavaImportPostProcessor.removeUnusedImports(javaCode)
        val expectedCode = """
            package com.example;

            import java.util.List;

            public class MyClass {
                public static void main(String[] args) {
                    List<String> myList = new ArrayList<>();
                }
            }
        """.trimIndent()

        assertEquals(expectedCode, modifiedCode)
    }

}
