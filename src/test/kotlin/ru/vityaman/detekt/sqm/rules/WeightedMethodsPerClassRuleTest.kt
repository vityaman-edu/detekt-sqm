package ru.vityaman.detekt.sqm.rules

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.lint
import io.kotest.matchers.collections.shouldHaveSingleElement
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test
import ru.vityaman.detekt.sqm.processors.WeightedMethodsPerClassProcessor


class WeightedMethodsPerClassRuleTest {
    private val subject = WeightedMethodsPerClassRule(Config.empty)

    @Test
    fun silent() {
        val code = """
            class BookCreature {
                fun id(): Long?
                fun ownerSub(): String?
                fun ownerEmail(): String?
                fun name(): String?
                fun coordinates(): Coordinates
                fun age(): Long?
                fun creatureType(): BookCreatureType
                fun creatureLocation(): MagicCity
                fun attackLevel(): Long
                fun defenseLevel(): Float
                fun ring(): Ring?
                fun creationDate(): OffsetDateTime?
            }
        """.trimIndent()

        val findings = lint(code)

        findings shouldHaveSingleElement { it.id == subject.ruleId }
    }

    fun lint(@Language("kotlin") code: String): List<Finding> {
        val file = compileContentForTest(code)
        WeightedMethodsPerClassProcessor().onProcess(file, BindingContext.EMPTY)
        return subject.lint(file)
    }
}
