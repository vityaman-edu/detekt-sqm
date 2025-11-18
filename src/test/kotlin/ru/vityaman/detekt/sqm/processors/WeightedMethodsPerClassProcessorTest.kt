package ru.vityaman.detekt.sqm.processors

import io.github.detekt.test.utils.compileContentForTest
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test

class WeightedMethodsPerClassProcessorTest {

    @Test
    fun simple() {
        val code = """
            class Person {
                fun name(): String
                fun age(): Int
            }
        """.trimIndent()

        val data = process(code)
        data shouldBeEqual mapOf(
            "<root>.Person" to 2,
        )
    }

    @Test
    fun nested() {
        val code = """
            class Person {
                fun name(): String
                fun age(): Int
            
                class Child {
                    fun toys(): List<String>
                }
            }
        """.trimIndent()

        val data = process(code)
        data shouldBeEqual mapOf(
            "<root>.Person" to 2,
            "<root>.Person.Child" to 1,
        )
    }

    private fun process(@Language("kotlin") code: String): Map<String, Int> {
        val ktFile = compileContentForTest(code)
        WeightedMethodsPerClassProcessor().onProcess(ktFile, BindingContext.EMPTY)
        val data = ktFile.getUserData(WeightedMethodsPerClassProcessor.dataKey)
        data shouldNotBeNull {}
        return data
    }
}
