package ru.vityaman.detekt.sqm.processors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.kotest.matchers.equals.shouldBeEqual
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.resolve.BindingContext

abstract class ProjectProcessorTest<T> {
    protected abstract val processor: ProjectProcessor<T>

    protected abstract val dependencies: List<() -> FileProcessListener>

    protected fun process(@Language("kotlin") code: String): T =
        process(listOf(code))

    protected fun process(sources: Iterable<String>): T {
        val context = BindingContext.EMPTY

        val files = sources.map { compileContentForTest(it) }

        val projects = files.map { it.project }.toSet()
        projects.size shouldBeEqual 1
        val project = projects.first()

        val processor = processor
        project.putUserData(processor.key, null)

        for (dependency in (dependencies + { processor })) {
            val processor = dependency()
            for (file in files) {
                processor.onProcess(file, context)
            }
        }

        return project.getUserData(processor.key)!!
    }
}
