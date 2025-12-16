package ru.vityaman.detekt.sqm.rules

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtClass
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.TypeKind
import ru.vityaman.detekt.sqm.processors.UserData

class InheritanceTreeDepthRule(config: Config) : Rule(config) {
    private val threshold: Int by config(defaultValue = 5)

    override val issue: Issue = Issue(
        id = "InheritanceTreeDepth",
        severity = Severity.Maintainability,
        description = "Too high Depth of Inheritance Tree",
        debt = Debt(days = 1),
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val full = klass.fqName?.toString() ?: return
        val tree = klass.project.getUserData(UserData.inheritanceTree) ?: return
        val kinds = klass.project.getUserData(UserData.typeKind) ?: return

        val depth = depth(full, tree, kinds, limit = threshold + 1)
        if (depth <= threshold) {
            return
        }

        report(
            ThresholdedCodeSmell(
                issue,
                entity = Entity.from(klass),
                metric = Metric(type = "SIZE", value = depth, threshold = threshold),
                message = with(StringBuilder()) {
                    append("The class ${klass.fqName} has inheritance ")
                    append("tree depth at least $depth. Threshold is ")
                    append("specified with $threshold.")
                    toString()
                },
                references = emptyList(),
            )
        )
    }

    private fun depth(
        name: FQName,
        tree: Map<FQName, Set<FQName>>,
        kinds: Map<FQName, TypeKind>,
        limit: Int,
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
