package ru.vityaman.detekt.sqm.processors

import io.github.detekt.test.utils.compileContentForTest
import io.kotest.matchers.equals.shouldBeEqual
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test
import ru.vityaman.detekt.sqm.visitors.FqNameVisitor

class QualificationProcessorTest {

    @Test
    fun single() {
        val code = """
            class A
        """.trimIndent()

        process(code) shouldBeEqual setOf(
            "A",
        )
    }

    @Test
    fun inheritance() {
        val code = """
            class A
            class B
            class C : A, B
            class D : C 
        """.trimIndent()

        process(code) shouldBeEqual setOf(
            "A",
            "B",
            "C",
            "D",
        )
    }

    @Test
    fun packages() {
        val code = """
            package a.b.c
            class A
            class B
            class C : A, B
            class D : C 
        """.trimIndent()

        process(code) shouldBeEqual setOf(
            "a.b.c.A",
            "a.b.c.B",
            "a.b.c.C",
            "a.b.c.D",
        )
    }

    @Test
    fun nestedClass() {
        val code = """
            class A {
                class B
                class C {
                    class D 
                }
            }
            class E
        """.trimIndent()

        process(code) shouldBeEqual setOf(
            "A",
            "A.B",
            "A.C",
            "A.C.D",
            "E",
        )
    }

    @Test
    fun importBasic() {
        val code = """
            import a.b.c.A
            import a.b.c.B
            class C : A, B
            class D : C
        """.trimIndent()

        process(code) shouldBeEqual setOf(
            "a.b.c.A",
            "a.b.c.B",
            "C",
            "D",
        )
    }

    @Test
    fun importInPackage() {
        val code = """
            package d.e.f
            import a.b.c.A
            import a.b.c.B
            class C : A, B
            class D : C
        """.trimIndent()

        process(code) shouldBeEqual setOf(
            "a.b.c.A",
            "a.b.c.B",
            "d.e.f.C",
            "d.e.f.D",
        )
    }

    @Test
    fun mix() {
        val code = """
            package d.e.f
            import a.b.c.A
            import a.b.c.B
            class C : A, B
            class D : C {
                class E
                class F : E
            }
        """.trimIndent()

        process(code) shouldBeEqual setOf(
            "a.b.c.A",
            "a.b.c.B",
            "d.e.f.C",
            "d.e.f.D",
            "d.e.f.D.E",
            "d.e.f.D.F",
        )
    }

    private fun process(@Language("kotlin") code: String): Set<String> {
        val file = compileContentForTest(code)

        QualificationProcessor().onProcess(file, BindingContext.EMPTY)

        return FqNameVisitor()
            .also { it.visitKtFile(file) }
            .names()
    }
}
