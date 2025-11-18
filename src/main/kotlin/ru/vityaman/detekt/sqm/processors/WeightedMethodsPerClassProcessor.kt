package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext

class WeightedMethodsPerClassProcessor : FileProcessListener {
    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        val visitor = KtClassVisitor()
        file.accept(visitor)

        val data = visitor.methodsByClass().mapValues { it.value.size }
        file.putUserData(dataKey, data)
    }

    private class KtClassVisitor : DetektVisitor() {
        private val methods: MutableMap<String, MutableList<String>> = mutableMapOf()
        private var klass: String? = null

        fun methodsByClass(): Map<String, List<String>> = methods

        override fun visitClass(klass: KtClass) {
            this.klass = klass.name!!
            super.visitClass(klass)
            this.klass = null
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            if (klass == null) {
                return
            }

            methods.getOrPut(klass!!) { mutableListOf() }.add(function.name!!)
        }
    }

    companion object {
        val dataKey = Key<Map<String, Int>>("Weighted Methods Per Class")
    }
}
