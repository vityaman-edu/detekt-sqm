package ru.vityaman.detekt.sqm.reports

import io.gitlab.arturbosch.detekt.api.Detektion
import ru.vityaman.detekt.sqm.processors.WeightedMethodsPerClassProcessor

fun weightedMethodsPerClassReport(detektion: Detektion): String? {
    val data = detektion.getData(WeightedMethodsPerClassProcessor.dataKey)
    if (data.isNullOrEmpty()) {
        return null
    }

    return with(StringBuilder()) {
        for ((klass, methods) in data) {
            append("$klass: $methods\n")
        }
        toString()
    }
}
