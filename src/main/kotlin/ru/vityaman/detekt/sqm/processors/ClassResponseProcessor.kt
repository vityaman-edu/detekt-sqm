package ru.vityaman.detekt.sqm.processors

import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import ru.vityaman.detekt.sqm.core.FQName

class ClassResponseProcessor : ProjectProcessor<Map<FQName, Int>>() {
    override val key: Key<Map<FQName, Int>>
        get() = UserData.classResponse

    override fun visit(file: KtFile, context: BindingContext): Map<FQName, Int> {
        val calledMethods = file.project.getUserData(UserData.calledMethods)
            ?: return emptyMap()

        val methods = file.project.getUserData(UserData.weightedMethodsPerClass)
            ?: return emptyMap()

        val keys = calledMethods.keys + methods.keys

        val responses: MutableMap<FQName, Int> = mutableMapOf()
        for (key in keys) {
            val called = calledMethods[key] ?: emptySet()
            val owned = methods[key] ?: 0

            responses[key] = called.size + owned
        }
        return responses
    }

    override fun merge(lhs: Map<FQName, Int>?, rhs: Map<FQName, Int>): Map<FQName, Int> =
        (lhs ?: emptyMap()) + rhs
}
