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

    override fun render(detektion: Detektion): String? = with(StringBuilder()) {
        append("# Software Quality Metrics Report\n")
        append("\n")

        val methods = detektion.getData(UserData.weightedMethodsPerClass)
        if (methods != null) {
            append("## Weighted Methods Per Class\n")
            append("\n")
            for ((klass, methods) in methods.toSortedMap()) {
                append("- `$klass`: $methods\n")
            }
            append("\n")
        }

        val tree = detektion.getData(UserData.inheritanceTree)
        if (tree != null) {
            append("## Inheritance Tree\n")
            append("\n")
            for ((klass, parents) in tree.toSortedMap()) {
                val parents = parents.ifEmpty { listOf("Object") }
                append("- `$klass`: ${parents.joinToString(", ")}\n")
            }
            append("\n")
        }

        val depths = detektion.getData(UserData.inheritanceDepth)
        if (depths != null) {
            append("## Depth of Inheritance Tree\n")
            append("\n")
            for ((klass, depth) in depths.toSortedMap()) {
                append("- `$klass`: $depth\n")
            }
            append("\n")
        }

        val children = detektion.getData(UserData.children)
        if (children != null) {
            append("## Children\n")
            append("\n")
            for ((klass, children) in children.toSortedMap()) {
                append("- `$klass`: ${children.joinToString(", ")}\n")
            }
            append("\n")
        }

        val childrenNumber = detektion.getData(UserData.childrenNumber)
        if (childrenNumber != null) {
            append("## Number of Children\n")
            append("\n")
            for ((klass, children) in childrenNumber.toSortedMap()) {
                append("- `$klass`: $children\n")
            }
            append("\n")
        }

        toString()
    }
}
