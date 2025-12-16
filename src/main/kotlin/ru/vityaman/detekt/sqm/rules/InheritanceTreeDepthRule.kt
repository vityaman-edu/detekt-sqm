package ru.vityaman.detekt.sqm.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtClass

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


    }
}
