package ru.vityaman.detekt.sqm.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtSuperTypeEntry
import ru.vityaman.detekt.sqm.processors.UserData

class FqNameVisitor : DetektVisitor() {
    private val names: MutableSet<String> = mutableSetOf()

    override fun visitElement(element: PsiElement) {
        visit(element)
        super.visitElement(element)
    }

    private fun visit(element: PsiElement) {
        names.add(element.getUserData(UserData.fqName) ?: return)
        names.addAll(element.getUserData(UserData.fqParentName)?.values?.toSet() ?: return)
    }

    fun names(): Set<String> = names
}
