package ru.vityaman.detekt.sqm.rules

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.internal.CompilerResources
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.getContextForPaths
import io.kotest.matchers.collections.shouldHaveSingleElement
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl
import org.junit.jupiter.api.Test
import ru.vityaman.detekt.sqm.processors.CouplingProcessor
import ru.vityaman.detekt.sqm.processors.QualificationProcessor
import ru.vityaman.detekt.sqm.processors.ReferencedTypesProcessor
import ru.vityaman.detekt.sqm.reports.SQMMarkdownReport

@KotlinCoreEnvironmentTest
class CouplingRuleTest(private val env: KotlinCoreEnvironment) {
    private val subject = CouplingRule(Config.empty)

    @Test
    fun activation() {
        val code = """
            class A01
            class A02
            class A03
            class A04
            class A05
            class A06
            class A07
            class A08
            class A09
            class A10
            class A11
            class A12
            class A13
            class A14
            class A15

            class B {
              fun f() {
                val a01 = A01(); a01
                val a02 = A02(); a02
                val a03 = A03(); a03
                val a04 = A04(); a04
                val a05 = A05(); a05
                val a06 = A06(); a06
                val a07 = A07(); a07
                val a08 = A08(); a08
                val a09 = A09(); a09
                val a10 = A10(); a10
                val a11 = A11(); a11
                val a12 = A12(); a12
                val a13 = A13(); a13
                val a14 = A14(); a14
                val a15 = A15(); a15
              }
            }
        """.trimIndent()

        lint(code) shouldHaveSingleElement {
            it.id == subject.ruleId &&
                    "has 15 number of classes to which a class is coupled" in it.message
        }
    }

    fun lint(@Language("kotlin") code: String): List<Finding> {
        val file = compileContentForTest(code)
        val context = env.getContextForPaths(listOf(file))
        val langVer = env.configuration.languageVersionSettings

        val dataFlowValueFactory = DataFlowValueFactoryImpl(langVer)
        val compilerResources = CompilerResources(langVer, dataFlowValueFactory)

        listOf(
            QualificationProcessor(),
            ReferencedTypesProcessor(),
            CouplingProcessor(),
        ).forEach { it.onProcess(file, context) }

        subject.visitFile(file, context, compilerResources)
        return subject.findings
    }
}
