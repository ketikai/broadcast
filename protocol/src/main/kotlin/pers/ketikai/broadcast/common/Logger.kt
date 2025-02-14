package pers.ketikai.broadcast.common

import pers.ketikai.broadcast.generated.ProtocolProperties
import java.io.PrintWriter
import java.io.StringWriter

interface Logger {

    companion object {
        private var debugIsEnabled = true

        fun enableDebug() {
            debugIsEnabled = true
        }

        fun disableDebug() {
            debugIsEnabled = false
        }

        val logger: Logger by lazy {
            Services.singleton(Logger::class.java, this::class.java.classLoader)
        }

        fun println(vararg message: String) {
            println(logger, *message)
        }

        fun println(logger: Logger, vararg message: String) {
            message.isEmpty() && return
            logger.println(*message)
        }

        fun debug(vararg message: String) {
            debug(logger, *message)
        }

        fun debug(logger: Logger, vararg message: String) {
            message.isEmpty() && return
            debugIsEnabled || return
            logger.println(*message.map {
                "${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.DEBUG.colorCode} $it"
            }.toTypedArray())
        }

        fun info(vararg message: String) {
            info(logger, *message)
        }

        fun info(logger: Logger, vararg message: String) {
            message.isEmpty() && return
            logger.println(*message.map {
                "${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.INFO.colorCode} $it"
            }.toTypedArray())
        }

        fun warn(vararg message: String) {
            warn(logger, *message)
        }

        fun warn(logger: Logger, vararg message: String) {
            message.isEmpty() && return
            logger.println(*message.map {
                "${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.WARN.colorCode} $it"
            }.toTypedArray())
        }

        fun error(vararg message: String) {
            error(logger, *message)
        }

        fun error(logger: Logger, vararg message: String) {
            message.isEmpty() && return
            logger.println(*message.map {
                "${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.ERROR.colorCode} $it"
            }.toTypedArray())
        }

        fun error(vararg throwable: Throwable) {
            error(logger, *throwable)
        }

        fun error(logger: Logger, vararg throwable: Throwable) {
            throwable.isEmpty() && return
            logger.println(*throwable.map {
                val stackTrace = StringWriter(256)
                PrintWriter(stackTrace).use { writer ->
                    it.printStackTrace(writer)
                }
                val message = it.message ?: ""
                return@map "${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.DEBUG.colorCode} $message $stackTrace"
            }.toTypedArray())
        }
    }

    fun println(vararg message: String)

    fun debug(vararg message: String) {
        Companion.debug(this, *message)
    }

    fun info(vararg message: String) {
        Companion.info(this, *message)
    }

    fun warn(vararg message: String) {
        Companion.warn(this, *message)
    }

    fun error(vararg message: String) {
        Companion.error(this, *message)
    }

    fun error(vararg throwable: Throwable) {
        Companion.error(this, *throwable)
    }
}