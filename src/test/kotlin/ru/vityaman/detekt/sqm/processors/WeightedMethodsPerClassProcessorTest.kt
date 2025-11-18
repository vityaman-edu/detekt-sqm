package ru.vityaman.detekt.sqm.processors

import io.github.detekt.test.utils.compileContentForTest
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test

class WeightedMethodsPerClassProcessorTest {

    @Test
    fun `smoke`() {
        val code = """
            class Person {
                fun name(): String
                fun age(): Int
            }
        """.trimIndent()

        val ktFile = compileContentForTest(code)
        WeightedMethodsPerClassProcessor().onProcess(ktFile, BindingContext.EMPTY)

        val data = ktFile.getUserData(WeightedMethodsPerClassProcessor.dataKey)
        data shouldNotBeNull {}
        data shouldBeEqual mapOf("Person" to 2)
    }
}
