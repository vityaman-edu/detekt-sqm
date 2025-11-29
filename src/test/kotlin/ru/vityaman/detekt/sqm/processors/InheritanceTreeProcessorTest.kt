package ru.vityaman.detekt.sqm.processors

import io.github.detekt.test.utils.compileContentForTest
import io.kotest.matchers.equals.shouldBeEqual
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test

class InheritanceTreeProcessorTest {

    @Test
    fun single() {
        val code = """
            class A
        """.trimIndent()

        process(code) shouldBeEqual mapOf(
            "A" to setOf(),
        )
    }

    @Test
    fun interesting() {
        val code = """
            class A
            class B
            class C : A, B
            class D : C
            class E
            class F : D, E
        """.trimIndent()

        process(code) shouldBeEqual mapOf(
            "A" to setOf(),
            "B" to setOf(),
            "C" to setOf("A", "B"),
            "D" to setOf("C"),
            "E" to setOf(),
            "F" to setOf("D", "E"),
        )
    }

    private fun process(@Language("kotlin") code: String): Map<String, Set<String>> {
        val file = compileContentForTest(code)

        QualificationProcessor().onProcess(file, BindingContext.EMPTY)

        return InheritanceTreeProcessor()
            .also { it.onProcess(file, BindingContext.EMPTY) }
            .parents()
    }
}
