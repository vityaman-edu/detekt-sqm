package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.Test
import ru.vityaman.detekt.sqm.core.FQName

class InheritanceTreeProcessorTest : ProjectProcessorTest<Map<FQName, Set<FQName>>>() {
    override val processor: InheritanceTreeProcessor
        get() = InheritanceTreeProcessor()

    override val dependencies: List<() -> FileProcessListener>
        get() = listOf { QualificationProcessor() }

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
        val sources = listOf(
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
}
