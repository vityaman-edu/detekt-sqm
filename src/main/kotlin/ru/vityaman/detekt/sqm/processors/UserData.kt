package ru.vityaman.detekt.sqm.processors

import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import ru.vityaman.detekt.sqm.core.FQName
import ru.vityaman.detekt.sqm.core.TypeKind

object UserData {
    val weightedMethodsPerClass = Key<Map<String, Int>>("WeightedMethodsPerClass")
    val methods = Key<List<String>>("Methods")
    val fqName = Key<FQName>("FullyQualifiedName")
    val inheritanceTree = Key<Map<FQName, Set<FQName>>>("InheritanceTree")
    val typeKind = Key<Map<FQName, TypeKind>>("TypeKind")
    val inheritanceDepth = Key<Map<FQName, Int>>("InheritanceTreeDepth")
    val children = Key<Map<FQName, Set<FQName>>>("Children")
    val childrenNumber = Key<Map<FQName, Int>>("ChildrenNumber")
}
