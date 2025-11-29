package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtSuperTypeEntry
import org.jetbrains.kotlin.resolve.BindingContext

class QualificationProcessor : FileProcessListener {
    private val qualified: MutableMap<String, String> = mutableMapOf()

    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        Visitor().visitKtFile(file)
    }

    inner class Visitor : DetektVisitor() {
        override fun visitImportDirective(importDirective: KtImportDirective) {
            val fqName = importDirective.importedFqName

            val name = fqName?.shortName()?.toString() ?: ""
            val full = fqName?.toString() ?: ""

            qualified[name] = full

            super.visitImportDirective(importDirective)
        }

        override fun visitClass(klass: KtClass) {
            val full = klass.fqName?.toString() ?: ""
            val name = klass.name ?: full

            qualified[name] = full
            klass.putUserData(UserData.fqName, full)

            super.visitClass(klass)
        }

        override fun visitSuperTypeEntry(specifier: KtSuperTypeEntry) {
            val type = specifier.typeReference?.typeElement

            val name = type?.text ?: ""
            val full = qualified[name]

            specifier.putUserData(UserData.fqName, full)

            super.visitSuperTypeEntry(specifier)
        }
    }
}
