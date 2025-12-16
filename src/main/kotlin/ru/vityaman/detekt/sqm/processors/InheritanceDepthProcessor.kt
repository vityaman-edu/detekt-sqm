package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.TypeKind

class InheritanceDepthProcessor : ProjectProcessor<Map<FQName, Int>>() {
    override val key: Key<Map<FQName, Int>>
        get() = UserData.inheritanceDepth

    override fun visit(file: KtFile): Map<FQName, Int> =
        Visitor().also { it.visitFile(file) }.depths

    override fun merge(lhs: Map<FQName, Int>?, rhs: Map<FQName, Int>): Map<FQName, Int> =
        (lhs ?: emptyMap()) + rhs

    private inner class Visitor : DetektVisitor() {
        val depths: MutableMap<FQName, Int> = mutableMapOf()

        override fun visitClass(klass: KtClass) {
            super.visitClass(klass)

            val full = klass.getUserData(UserData.fqName) ?: return
            val tree = klass.project.getUserData(UserData.inheritanceTree) ?: return
            val kinds = klass.project.getUserData(UserData.typeKind) ?: return

            depths[full] = depth(full, tree, kinds)
        }

        private fun depth(
            name: FQName,
            tree: Map<FQName, Set<FQName>>,
            kinds: Map<FQName, TypeKind>,
            limit: Int = 1000,
        ): Int {
            if (limit <= 0) {
                return 1
            }

            val kind = kinds[name] ?: return 1
            if (kind == TypeKind.INTERFACE) {
                return 1
            }

            require(kind == TypeKind.CLASS)
            val parents = tree[name] ?: return 1

            return 1 + (parents.maxOfOrNull { depth(it, tree, kinds, limit - 1) } ?: 0)
        }
    }
}
