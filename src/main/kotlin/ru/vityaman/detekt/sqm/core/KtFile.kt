package ru.vityaman.detekt.sqm.core

import org.jetbrains.kotlin.psi.KtFile

fun KtFile.relativePath(): String =
    name.substringAfter("src/main/kotlin/")
