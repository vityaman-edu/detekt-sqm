package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class InheritanceTreeProcessor : FileProcessListener {
    private val parents: MutableMap<String, Set<String>> = mutableMapOf()

    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        Visitor().visitKtFile(file)
    }

    override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        result.addData(UserData.inheritanceTree, parents)
    }

    fun parents(): Map<String, Set<String>> = parents

    inner class Visitor : DetektVisitor() {
        override fun visitClass(klass: KtClass) {
            val fqName = klass.getUserData(UserData.fqName) ?: ""

            val fqParents = klass
                .superTypeListEntries
                .mapNotNull { it.getUserData(UserData.fqName) }
                .toSet()

            parents[fqName] = fqParents

            super.visitClass(klass)
        }
    }
}
