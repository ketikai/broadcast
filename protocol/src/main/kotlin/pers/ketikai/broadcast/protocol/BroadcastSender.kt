package pers.ketikai.broadcast.protocol

import pers.ketikai.broadcast.common.Logger

interface BroadcastSender {

    val logger: Logger
    val name: String
    val isAdmin: Boolean
    val isPlayer: Boolean

    fun hasPermission(permission: String): Boolean
}