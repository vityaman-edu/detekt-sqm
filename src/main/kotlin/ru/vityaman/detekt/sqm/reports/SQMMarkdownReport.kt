package ru.vityaman.detekt.sqm.reports

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.shortenFQ
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
                append("- `${map(klass)}`: $methods\n")
            }
            append("\n")
        }

        val tree = detektion.getData(UserData.inheritanceTree)
        if (tree != null) {
            append("## Inheritance Tree\n")
            append("\n")
            for ((klass, parents) in tree.toSortedMap()) {
                val parents = parents.ifEmpty { setOf("Object") }
                append("- `${map(klass)}`: ${map(parents)}\n")
            }
            append("\n")
        }

        val depths = detektion.getData(UserData.inheritanceDepth)
        if (depths != null) {
            append("## Depth of Inheritance Tree\n")
            append("\n")
            for ((klass, depth) in depths.toSortedMap()) {
                append("- `${map(klass)}`: $depth\n")
            }
            append("\n")
        }

        val children = detektion.getData(UserData.children)
        if (children != null) {
            append("## Children\n")
            append("\n")
            for ((klass, children) in children.toSortedMap()) {
                append("- `${map(klass)}`: ${map(children)}\n")
            }
            append("\n")
        }

        val childrenNumber = detektion.getData(UserData.childrenNumber)
        if (childrenNumber != null) {
            append("## Number of Children\n")
            append("\n")
            for ((klass, children) in childrenNumber.toSortedMap()) {
                append("- `${map(klass)}`: $children\n")
            }
            append("\n")
        }

        val referencedTypes = detektion.getData(UserData.referencedTypes)
        if (referencedTypes != null) {
            append("## Referenced Types By Class\n")
            append("\n")
            for ((klass, types) in referencedTypes.toSortedMap()) {
                append("- `${map(klass)}`: ${map(types)}\n")
            }
            append("\n")
        }

        val coupling = detektion.getData(UserData.coupling)
        if (coupling != null) {
            append("## Coupling between Object Classes\n")
            append("\n")
            for ((klass, coupling) in coupling.toSortedMap()) {
                append("- `${map(klass)}`: $coupling\n")
            }
            append("\n")
        }

        toString()
    }

    private fun map(name: FQName): String =
        shortenFQ(name)

    private fun map(names: Set<FQName>): String =
        names.toSortedSet().joinToString(", ") { map(it) }
}
