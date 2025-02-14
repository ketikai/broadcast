package pers.ketikai.broadcast.protocol

interface BroadcastJobContainer {

    val jobs: Map<String, String>

    fun remove(id: String): Boolean

    fun clear()
}