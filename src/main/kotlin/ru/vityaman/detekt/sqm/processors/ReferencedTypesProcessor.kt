package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.js.descriptorUtils.getKotlinTypeFqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getType
import ru.vityaman.detekt.sqm.core.FQName

class ReferencedTypesProcessor : ProjectProcessor<Map<FQName, Set<FQName>>>() {
    override val key: Key<Map<FQName, Set<FQName>>>
        get() = UserData.referencedTypes

    override fun visit(file: KtFile, context: BindingContext): Map<FQName, Set<FQName>> =
        Visitor(context).also { it.visitFile(file) }.typesByClass

    override fun merge(
        lhs: Map<FQName, Set<FQName>>?,
        rhs: Map<FQName, Set<FQName>>,
    ): Map<FQName, Set<FQName>> =
        (lhs ?: emptyMap()) + rhs

    private inner class Visitor(
        private val context: BindingContext,
        val typesByClass: MutableMap<FQName, Set<FQName>> = mutableMapOf()
    ) : DetektVisitor() {
        private val types: MutableSet<FQName> = mutableSetOf()

        override fun visitClassOrObject(classOrObject: KtClassOrObject) {
            val fqName = classOrObject.getUserData(UserData.fqName)

            val visitor = Visitor(context, typesByClass)
            for (child in classOrObject.children) {
                visitor.visitElement(child)
            }

            typesByClass[fqName ?: return] = visitor.types
        }

        override fun visitExpression(expression: KtExpression) {
            val type = expression
                .getType(context)
                ?.getKotlinTypeFqName(printTypeArguments = false)

            if (type != null && !type.startsWith("kotlin.") && !type.isEmpty()) {
                types.add(type)
            }

            super.visitExpression(expression)
        }
    }
}
