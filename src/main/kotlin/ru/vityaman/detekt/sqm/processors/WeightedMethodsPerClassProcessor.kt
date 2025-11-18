package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext

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
        private var klass: String? = null

        fun methodsByClass(): Map<String, List<String>> = methods

        override fun visitClass(klass: KtClass) {
            if (klass.fqName == null) {
                return
            }

            this.klass = klass.fqName.toString()
            super.visitClass(klass)
            klass.putUserData(UserData.methods, methods[this.klass])
            this.klass = null
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            if (klass == null) {
                return
            }

            methods.getOrPut(klass!!) { mutableListOf() }.add(function.name!!)
        }
    }
}
