package ru.vityaman.detekt.sqm.processors

import io.github.detekt.test.utils.compileContentForTest
import io.kotest.matchers.equals.shouldBeEqual
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.TypeKind

class TypeKindProcessorTest {

    @Test
    fun example() {
        val code = """
            interface IService            
            class TService
        """.trimIndent()

        process(code) shouldBeEqual mapOf(
            "IService" to TypeKind.INTERFACE,
            "TService" to TypeKind.CLASS,
        )
    }

    private fun process(@Language("kotlin") source: String): Map<FQName, TypeKind> =
        process(arrayOf(source))

    private fun process(sources: Array<String>): Map<FQName, TypeKind> {
        val files = sources.map { compileContentForTest(it) }

        val context = BindingContext.EMPTY

        val qualification = QualificationProcessor()
        files.forEach { qualification.onProcess(it, context) }

        val typeKinds = TypeKindProcessor()
        files.forEach { typeKinds.onProcess(it, context) }

        return typeKinds.kinds()
    }
}
