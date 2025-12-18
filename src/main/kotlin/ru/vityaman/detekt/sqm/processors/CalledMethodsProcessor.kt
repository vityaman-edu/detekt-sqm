package ru.vityaman.detekt.sqm.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.js.descriptorUtils.getKotlinTypeFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getType
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.MethodCall

class CalledMethodsProcessor : ProjectProcessor<Map<FQName, Set<MethodCall>>>() {
    override val key: Key<Map<FQName, Set<MethodCall>>>
        get() = UserData.calledMethods

    override fun visit(file: KtFile, context: BindingContext): Map<FQName, Set<MethodCall>> =
        Visitor(context).also { it.visitFile(file) }.calledMethodsByClass

    override fun merge(
        lhs: Map<FQName, Set<MethodCall>>?,
        rhs: Map<FQName, Set<MethodCall>>,
    ): Map<FQName, Set<MethodCall>> =
        (lhs ?: emptyMap()) + rhs

    private class Visitor(
        private val context: BindingContext,
        val calledMethodsByClass: MutableMap<FQName, MutableSet<MethodCall>> = mutableMapOf()
    ) : DetektVisitor() {
        private val methods: MutableSet<MethodCall> = mutableSetOf()

        override fun visitClassOrObject(classOrObject: KtClassOrObject) {
            val fqName = classOrObject.getUserData(UserData.fqName)

            val visitor = Visitor(context, calledMethodsByClass)
            for (child in classOrObject.children) {
                visitor.visitElement(child)
            }

            calledMethodsByClass[fqName ?: return] = visitor.methods
        }

        override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
            val receiverType = expression
                .receiverExpression
                .getType(context)
                ?.getKotlinTypeFqName(printTypeArguments = false)

            val callee = (
                    (expression.selectorExpression as? KtCallExpression)
                        ?.calleeExpression as? KtReferenceExpression)

            val method = callee?.text

            if (receiverType != null && method != null
                && !receiverType.isEmpty() && !receiverType.startsWith("kotlin.")
                && method != "also") {
                methods.add(MethodCall(klass = receiverType, name = method))
            }

            super.visitDotQualifiedExpression(expression)
        }
    }
}
