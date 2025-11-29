package ru.vityaman.detekt.sqm.processors

import org.jetbrains.kotlin.com.intellij.openapi.util.Key

object UserData {
    val weightedMethodsPerClass = Key<Map<String, Int>>("WeightedMethodsPerClass")
    val methods = Key<List<String>>("Methods")
    val fqName = Key<String>("FullyQualifiedName")
    val inheritanceTree = Key<Map<String, Set<String>>>("InheritanceTree")
}
