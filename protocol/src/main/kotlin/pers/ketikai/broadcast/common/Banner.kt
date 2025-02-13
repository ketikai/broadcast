package pers.ketikai.broadcast.common

import kotlin.streams.toList

@Suppress("MemberVisibilityCanBePrivate")
object Banner {

    const val FILE_NAME = "/banner.txt"

    private val _lines by lazy {
        this::class.java.getResourceAsStream(FILE_NAME)?.use {
            it.bufferedReader().lines().toList()
        } ?: emptyList()
    }

    val lines
        get() = _lines.toTypedArray()
}