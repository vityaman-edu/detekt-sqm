package ru.vityaman.detekt.sqm.reports

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport

class WeightedMethodsPerClassOutputReport : OutputReport() {
    override val id: String = "WeightedMethodsPerClass"

    override val ending: String
        get() = ""

    override fun render(detektion: Detektion): String? =
        weightedMethodsPerClassReport(detektion)
}
