package pers.ketikai.broadcast.spigot.service

import pers.ketikai.broadcast.protocol.BroadcastJobContainer

class BroadcastJobContainer: BroadcastJobContainer {
    override val jobs: Map<String, String> = emptyMap()

    override fun remove(id: String): Boolean {
        return false
    }

    override fun clear() {
        // nothing to do
    }
}