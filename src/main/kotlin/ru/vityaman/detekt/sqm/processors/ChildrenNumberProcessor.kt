package ru.vityaman.detekt.sqm.processors

import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import ru.vityaman.detekt.sqm.core.FQName

class ChildrenNumberProcessor : ProjectProcessor<Map<FQName, Int>>() {
    override val key: Key<Map<FQName, Int>>
        get() = UserData.childrenNumber

    override fun visit(file: KtFile, context: BindingContext): Map<FQName, Int> =
        emptyMap()

    override fun merge(lhs: Map<FQName, Int>?, rhs: Map<FQName, Int>): Map<FQName, Int> =
        (lhs ?: emptyMap()) + rhs

    override fun finish(project: Project): Map<FQName, Int> {
        val childrenNumber: MutableMap<FQName, Int> = mutableMapOf()

        val children = project.getUserData(UserData.children)!!
        for ((klass, children) in children) {
            childrenNumber[klass] = children.size
        }

        return childrenNumber
    }
}
