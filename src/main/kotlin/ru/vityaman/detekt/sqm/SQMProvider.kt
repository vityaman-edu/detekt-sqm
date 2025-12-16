package ru.vityaman.detekt.sqm

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import ru.vityaman.detekt.sqm.rules.InheritanceTreeDepthRule
import ru.vityaman.detekt.sqm.rules.WeightedMethodsPerClassRule

class SQMProvider : RuleSetProvider {
    override val ruleSetId: String
        get() = "SQM"

    override fun instance(config: Config): RuleSet =
        RuleSet(
            ruleSetId,
            listOf(
                WeightedMethodsPerClassRule(config),
                InheritanceTreeDepthRule(config),
            ),
        )
}
