package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.resolve.BindingContext
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.TypeKind

class TypeKindProcessor : FileProcessListener {
    private val kinds: MutableMap<FQName, TypeKind> = mutableMapOf()

    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        Visitor().visitKtFile(file)
    }

    override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        result.addData(UserData.typeKind, kinds)
    }

    fun kinds(): Map<FQName, TypeKind> = kinds

    inner class Visitor : DetektVisitor() {
        override fun visitClass(klass: KtClass) {
            val fqName = klass.getUserData(UserData.fqName) ?: ""
            kind(klass)?.let { kinds[fqName] = it }
        }

        private fun kind(klass: KtClass): TypeKind? {
            return if (klass.isEnum()) {
                null
            } else if (klass.isInterface()) {
                TypeKind.INTERFACE
            } else if (klass.isAbstract()) {
                null
            } else {
                TypeKind.CLASS
            }
        }
    }
}
