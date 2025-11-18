package ru.vityaman.detekt.sqm.reports

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

class WeightedMethodsPerClassConsoleReport : ConsoleReport() {
    override val id: String = "WeightedMethodsPerClassConsoleReport"

    override fun render(detektion: Detektion): String? =
        weightedMethodsPerClassReport(detektion)
}
