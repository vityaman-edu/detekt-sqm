package ru.vityaman.detekt.sqm.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtClass
import ru.vityaman.detekt.sqm.core.Log
import ru.vityaman.detekt.sqm.processors.UserData

class CouplingRule(config: Config) : Rule(config) {
    private val threshold: Int by config(defaultValue = 14)

    override val issue: Issue = Issue(
        id = "Coupling",
        severity = Severity.Maintainability,
        description = "Too high number of classes to which a class is coupled",
        debt = Debt(days = 1),
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val full = klass.fqName?.toString() ?: return
        val couplings = klass.project.getUserData(UserData.coupling) ?: return

        val coupling  = couplings[full] ?: return
        if (coupling <= threshold) {
            return
        }

        report(
            ThresholdedCodeSmell(
                issue,
                entity = Entity.from(klass),
                metric = Metric(type = "SIZE", value = coupling, threshold = threshold),
                message = with(StringBuilder()) {
                    append("The class ${klass.fqName} has ")
                    append("$coupling number of classes to ")
                    append("which a class is coupled. ")
                    append("Threshold is specified with $threshold.")
                    toString()
                },
                references = emptyList(),
            )
        )
    }
}
