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

class WeightedMethodsPerClassRule(config: Config) : Rule(config) {
    private val threshold: Int by config(defaultValue = 7)

    override val issue: Issue = Issue(
        id = "WeightedMethodsPerClass",
        severity = Severity.Maintainability,
        description = "Too heavy by methods class",
        debt = Debt.TWENTY_MINS,
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val data = klass.getUserData(UserData.methods)
        requireNotNull(data)

        val count = data.size
        if (count <= threshold) {
            return
        }

        report(
            ThresholdedCodeSmell(
                issue,
                entity = Entity.from(klass),
                metric = Metric(type = "SIZE", value = count, threshold = threshold),
                message = with(StringBuilder()) {
                    append("The file ${klass.fqName} has $count functions. ")
                    append("Threshold is specified with $threshold.")
                    toString()
                },
                references = emptyList(),
            )
        )
    }
}
