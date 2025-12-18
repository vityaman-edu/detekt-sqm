package ru.vityaman.detekt.sqm.processors

import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import ru.vityaman.detekt.sqm.core.FQName

class CouplingProcessor : ProjectProcessor<Map<FQName, Int>>() {
    override val key: Key<Map<FQName, Int>>
        get() = UserData.coupling

    override fun visit(file: KtFile, context: BindingContext): Map<FQName, Int> =
        file.project
            .getUserData(UserData.referencedTypes)
            ?.mapValues { it.value.size }
            ?: emptyMap()

    override fun merge(lhs: Map<FQName, Int>?, rhs: Map<FQName, Int>): Map<FQName, Int> =
        (lhs ?: emptyMap()) + rhs
}
