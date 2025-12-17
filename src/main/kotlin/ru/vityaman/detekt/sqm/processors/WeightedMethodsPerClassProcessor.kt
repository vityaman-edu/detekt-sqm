package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.resolve.BindingContext
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.Log

class WeightedMethodsPerClassProcessor : FileProcessListener {
    override fun onStart(files: List<KtFile>, bindingContext: BindingContext) {
        Log.debug { "Running ${UserData.weightedMethodsPerClass}..." }
    }

    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        val visitor = KtClassVisitor()
        file.accept(visitor)

        val data = visitor.methodsByClass()
            .mapKeys { it.key }
            .mapValues { it.value.size }
        file.putUserData(UserData.weightedMethodsPerClass, data)
    }

    override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        val methods = mutableMapOf<FQName, Int>()
        for (file in files) {
            val data = file.getUserData(UserData.weightedMethodsPerClass) ?: continue
            methods += data
        }
        result.addData(UserData.weightedMethodsPerClass, methods)
    }

    private class KtClassVisitor : DetektVisitor() {
        private val methods: MutableMap<String, MutableList<String>> = mutableMapOf()
        private var classes: MutableList<String> = mutableListOf()

        fun methodsByClass(): Map<FQName, List<String>> = methods

        override fun visitClass(klass: KtClass) {
            if (klass.fqName == null) {
                return
            }

            classes.addLast(klass.fqName.toString())
            super.visitClass(klass)

            val methods = methods.getOrDefault(classes.last(), listOf())
            klass.putUserData(UserData.methods, methods)

            classes.removeLast()
        }

        override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
            declaration.fqName ?: return

            classes.addLast(declaration.fqName.toString())
            super.visitObjectDeclaration(declaration)
            classes.removeLast()
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            if (classes.isEmpty()) {
                return
            }

            methods.getOrPut(classes.last()) { mutableListOf() }.add(function.name!!)
        }
    }
}
