package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.BindingContext
import ru.vityaman.detekt.sqm.core.Log

class WeightedMethodsPerClassProcessor : FileProcessListener {
    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        val pkg = file.packageFqName.toString()

        val visitor = KtClassVisitor()
        file.accept(visitor)

        val data = visitor.methodsByClass()
            .mapKeys { "$pkg.${it.key}" }
            .mapValues { it.value.size }
        file.putUserData(UserData.weightedMethodsPerClass, data)
    }

    private class KtClassVisitor : DetektVisitor() {
        private val methods: MutableMap<String, MutableList<String>> = mutableMapOf()
        private var classes: MutableList<String> = mutableListOf()

        fun methodsByClass(): Map<String, List<String>> = methods

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
