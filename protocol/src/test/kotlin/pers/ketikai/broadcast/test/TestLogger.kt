package pers.ketikai.broadcast.test

import pers.ketikai.broadcast.common.Logger

class TestLogger: Logger {

    override fun println(vararg message: String) {
        message.forEach {
            kotlin.io.println(it)
        }
    }
}