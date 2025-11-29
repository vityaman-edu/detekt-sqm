package ru.vityaman.detekt.sqm.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtSuperTypeEntry
import ru.vityaman.detekt.sqm.processors.UserData

class FqNameVisitor : DetektVisitor() {
    private val names: MutableSet<String> = mutableSetOf()

    override fun visitSuperTypeEntry(specifier: KtSuperTypeEntry) {
        visit(specifier)
        super.visitSuperTypeEntry(specifier)
    }

    override fun visitElement(element: PsiElement) {
        visit(element)
        super.visitElement(element)
    }

    private fun visit(element: PsiElement) {
        val data = element.getUserData(UserData.fqName)
        if (data == null) {
            return
        }

        names.add(data)
    }

    fun names(): Set<String> = names
}
