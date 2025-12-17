package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.resolve.BindingContext
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.Log

class QualificationProcessor : FileProcessListener {
    private val qualified: MutableMap<String, String> = mutableMapOf()

    override fun onStart(files: List<KtFile>, bindingContext: BindingContext) {
        Log.debug { "Running ${UserData.fqName}..." }
    }

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

            val fqParentNames: MutableMap<String, FQName> = mutableMapOf()
            for (entry in klass.superTypeListEntries) {
                val name = entry.typeAsUserType?.referencedName ?: ""
                fqParentNames[name] = qualified[name] ?: name
            }
            klass.putUserData(UserData.fqParentName, fqParentNames)

            super.visitClass(klass)
        }
    }
}
