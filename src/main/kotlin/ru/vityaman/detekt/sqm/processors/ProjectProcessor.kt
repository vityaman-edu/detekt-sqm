package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import ru.vityaman.detekt.sqm.core.Log

abstract class ProjectProcessor<T> : FileProcessListener {
    abstract val key: Key<T>

    protected abstract fun visit(file: KtFile): T
    protected abstract fun merge(lhs: T?, rhs: T): T

    override fun onStart(files: List<KtFile>, bindingContext: BindingContext) {
        Log.debug { "Running $key" }
    }

    final override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        val lhs = file.project.getUserData(key)
        file.project.putUserData(key, merge(lhs, visit(file)))
    }

    final override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        files
            .map { it.project }
            .toSet()
            .mapNotNull { it.getUserData(key) }
            .reduceOrNull<T, T> { lhs, rhs -> merge(lhs, rhs) }
            ?.let { result.addData(key, it) }
    }
}
