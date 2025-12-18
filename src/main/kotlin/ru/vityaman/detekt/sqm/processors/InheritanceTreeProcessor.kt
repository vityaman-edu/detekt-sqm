package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import ru.vityaman.detekt.sqm.core.FQName

class InheritanceTreeProcessor : ProjectProcessor<Map<FQName, Set<FQName>>>() {
    override val key: Key<Map<FQName, Set<FQName>>>
        get() = UserData.inheritanceTree

    override fun visit(file: KtFile, context: BindingContext): Map<FQName, Set<FQName>> =
        Visitor().also { it.visitFile(file) }.parents

    override fun merge(
        lhs: Map<FQName, Set<FQName>>?,
        rhs: Map<FQName, Set<FQName>>
    ): Map<FQName, Set<FQName>> =
        (lhs ?: emptyMap()) + rhs

    private inner class Visitor : DetektVisitor() {
        val parents: MutableMap<FQName, Set<FQName>> = mutableMapOf()

        override fun visitClass(klass: KtClass) {
            val fqName = klass.getUserData(UserData.fqName) ?: ""
            val fqParentNames = klass.getUserData(UserData.fqParentName) ?: emptyMap()

            parents[fqName] = fqParentNames.values.toSet()
            super.visitClass(klass)
        }
    }
}
