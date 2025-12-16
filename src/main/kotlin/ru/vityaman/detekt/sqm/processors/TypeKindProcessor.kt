package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.TypeKind

class TypeKindProcessor : ProjectProcessor<Map<FQName, TypeKind>>() {
    override val key: Key<Map<FQName, TypeKind>>
        get() = UserData.typeKind

    override fun visit(file: KtFile): Map<FQName, TypeKind> =
        Visitor().also { it.visitFile(file) }.kinds

    override fun merge(
        lhs: Map<FQName, TypeKind>?,
        rhs: Map<FQName, TypeKind>
    ): Map<FQName, TypeKind> =
        (lhs ?: emptyMap()) + rhs

    private inner class Visitor : DetektVisitor() {
        val kinds: MutableMap<FQName, TypeKind> = mutableMapOf()

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
