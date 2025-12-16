package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.Test
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.TypeKind

class TypeKindProcessorTest : ProjectProcessorTest<Map<FQName, TypeKind>>() {

    override val processor: ProjectProcessor<Map<FQName, TypeKind>>
        get() = TypeKindProcessor()

    override val dependencies: List<() -> FileProcessListener>
        get() = listOf { QualificationProcessor() }

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
}
