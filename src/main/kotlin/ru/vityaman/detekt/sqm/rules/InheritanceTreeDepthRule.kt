package ru.vityaman.detekt.sqm.rules

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtClass
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
        val depths = klass.project.getUserData(UserData.inheritanceDepth) ?: return
        val depth = depths[full] ?: return
        if (depth <= threshold) {
            return
        }

        report(
            ThresholdedCodeSmell(
                issue,
                entity = Entity.from(klass),
                metric = Metric(type = "SIZE", value = depth, threshold = threshold),
                message = with(StringBuilder()) {
                    append("The class ${klass.fqName} has ")
                    append("inheritance tree depth $depth. ")
                    append("Threshold is specified with $threshold.")
                    toString()
                },
                references = emptyList(),
            )
        )
    }
}
