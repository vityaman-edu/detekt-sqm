package ru.vityaman.detekt.sqm.rules

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.lint
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test
import ru.vityaman.detekt.sqm.processors.InheritanceDepthProcessor
import ru.vityaman.detekt.sqm.processors.InheritanceTreeProcessor
import ru.vityaman.detekt.sqm.processors.QualificationProcessor
import ru.vityaman.detekt.sqm.processors.TypeKindProcessor

class InheritanceTreeDepthRuleTest {
    private val subject = InheritanceTreeDepthRule(Config.empty)

    @Test
    fun silent() {
        val code = """
            package silent
            class A
            class B : A
            class C : B 
        """.trimIndent()

        lint(code) shouldBe emptyList()
    }

    @Test
    fun threshold() {
        val code = """
            package threshold
            class D1
            class D2 : D1
            class D3 : D2
            class D4 : D3
            class D5 : D4
            class D6 : D5
        """.trimIndent()

        lint(code) shouldHaveSingleElement {
            it.id == subject.ruleId && "tree depth 6" in it.message
        }
    }

    @Test
    fun activation() {
        val code = """
            package activation
            class D1
            class D2 : D1
            class D3 : D2
            class D4 : D3
            class D5 : D4
            class D6 : D5
            class D7 : D6
            class D8 : D7
        """.trimIndent()

        val finding = lint(code)

        finding shouldHaveSize 3
        finding[0].message shouldContain "tree depth 6"
        finding[1].message shouldContain "tree depth 7"
        finding[2].message shouldContain "tree depth 8"
    }

    fun lint(@Language("kotlin") code: String): List<Finding> {
        val context = BindingContext.EMPTY

        val file = compileContentForTest(code)

        listOf(
            QualificationProcessor(),
            TypeKindProcessor(),
            InheritanceTreeProcessor(),
            InheritanceDepthProcessor(),
        ).forEach { it.onProcess(file, context) }

        return subject.lint(file)
    }
}
