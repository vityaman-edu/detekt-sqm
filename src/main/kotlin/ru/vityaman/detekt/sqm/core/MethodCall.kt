package ru.vityaman.detekt.sqm.core

data class MethodCall(
    val klass: FQName,
    val name: String,
)
