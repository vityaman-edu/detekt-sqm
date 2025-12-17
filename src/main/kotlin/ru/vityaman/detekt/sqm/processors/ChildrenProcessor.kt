package ru.vityaman.detekt.sqm.processors

import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import ru.vityaman.detekt.sqm.core.FQName

class ChildrenProcessor : ProjectProcessor<Map<FQName, Set<FQName>>>() {
    override val key: Key<Map<FQName, Set<FQName>>>
        get() = UserData.children

    override fun visit(file: KtFile): Map<FQName, Set<FQName>> =
        emptyMap()

    override fun merge(
        lhs: Map<FQName, Set<FQName>>?,
        rhs: Map<FQName, Set<FQName>>,
    ): Map<FQName, Set<FQName>> =
        (lhs ?: emptyMap()) + rhs

    override fun finish(project: Project): Map<FQName, Set<FQName>> {
        val children: MutableMap<FQName, MutableSet<FQName>> = mutableMapOf()

        val parents = project.getUserData(UserData.inheritanceTree)!!
        for ((klass, parents) in parents) {
            for (parent in parents) {
                children
                    .getOrPut(parent) { mutableSetOf() }
                    .add(klass)
            }
        }

        return children
    }
}
