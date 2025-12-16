package ru.vityaman.detekt.sqm.reports

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import ru.vityaman.detekt.sqm.processors.UserData
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

class SQMMarkdownReport : OutputReport() {
    override val id: String
        get() = "SQMMarkdownReport"

    override val ending: String
        get() = "md"

    override fun render(detektion: Detektion): String? {
        val methods = detektion.getData(UserData.weightedMethodsPerClass)

        return with(StringBuilder()) {
            append("# Software Quality Metrics Report\n")
            append("\n")

            if (methods != null) {
                append("## Weighted Methods Per Class\n")
                append("\n")
                for ((klass, methods) in methods) {
                    append("- `$klass`: $methods\n")
                }
                append("\n")
            }

            toString()
        }
    }
}
