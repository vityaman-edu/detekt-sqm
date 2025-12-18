package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.BindingContext
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.Log

class WeightedMethodsPerClassProcessor : FileProcessListener {
    private val key = UserData.weightedMethodsPerClass

    fun visit(file: KtFile): Map<FQName, Int> {
        val visitor = Visitor()
        file.accept(visitor)

        return visitor.methodsByClass()
            .mapKeys { it.key }
            .mapValues { it.value.size }
    }

    fun merge(lhs: Map<FQName, Int>?, rhs: Map<FQName, Int>): Map<FQName, Int> =
        (lhs ?: emptyMap()) + rhs

    override fun onStart(files: List<KtFile>, bindingContext: BindingContext) {
        Log.debug { "Running $key..." }
    }

    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        val lhs = file.project.getUserData(key)

        val rhs = visit(file)
        file.putUserData(key, rhs)

        file.project.putUserData(key, merge(lhs, rhs))
    }

    override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        val methods = mutableMapOf<FQName, Int>()
        for (file in files) {
            val data = file.getUserData(key) ?: continue
            methods += data
        }
        result.addData(key, methods)
    }

    private class Visitor : DetektVisitor() {
        private val methods: MutableMap<FQName, MutableList<String>> = mutableMapOf()
        private var classes: MutableList<FQName> = mutableListOf()

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
