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

    @Test
    fun multifile() {
        val sources = arrayOf(
            """
            package a.b.c
            import x.y.z.Y
            class A : Y
            class B
            """.trimIndent(),
            """
            package x.y.z
            import a.b.c.B
            class X : B
            class Y
            """.trimIndent(),
        )

        process(sources) shouldBeEqual mapOf(
            "a.b.c.A" to setOf("x.y.z.Y"),
            "a.b.c.B" to setOf(),
            "x.y.z.X" to setOf("a.b.c.B"),
            "x.y.z.Y" to setOf(),
        )
    }

    private fun process(@Language("kotlin") code: String): Map<String, Set<String>> =
        process(arrayOf(code))

    private fun process(sources: Array<String>): Map<String, Set<String>> {
        val files = sources.map { compileContentForTest(it) }

        val context = BindingContext.EMPTY

        val qualification = QualificationProcessor()
        files.forEach { qualification.onProcess(it, context) }

        val inheritance = InheritanceTreeProcessor()
        files.forEach { inheritance.onProcess(it, context) }

        return inheritance.parents()
    }
}
