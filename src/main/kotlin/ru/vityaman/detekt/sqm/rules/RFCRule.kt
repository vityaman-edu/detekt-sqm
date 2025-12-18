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
import ru.vityaman.detekt.sqm.processors.UserData

class RFCRule(config: Config) : Rule(config) {
    private val threshold: Int by config(defaultValue = 16)

    override val issue: Issue = Issue(
        id = "RFC",
        severity = Severity.Maintainability,
        description = "High RFC increases the density of bugs and decreases quality",
        debt = Debt.TWENTY_MINS,
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val full = klass.fqName?.toString() ?: return
        val rfcs = klass.project.getUserData(UserData.classResponse) ?: return

        val rfc  = rfcs[full] ?: return
        if (rfc <= threshold) {
            return
        }

        report(
            ThresholdedCodeSmell(
                issue,
                entity = Entity.from(klass),
                metric = Metric(type = "SIZE", value = rfc, threshold = threshold),
                message = with(StringBuilder()) {
                    append("The class ${klass.fqName} has ")
                    append("too high RFC $rfc. The high density ")
                    append("of bugs and decreased quality is ")
                    append("expected. Threshold is specified ")
                    append("with $threshold.")
                    toString()
                },
                references = emptyList(),
            )
        )
    }
}
