package pers.ketikai.broadcast.common

import pers.ketikai.broadcast.generated.ProtocolProperties
import java.io.PrintWriter
import java.io.StringWriter

interface Logger {

    companion object {
        private var debugIsEnabled = true

        @JvmStatic
        fun enableDebug() {
            debugIsEnabled = true
        }

        @JvmStatic
        fun disableDebug() {
            debugIsEnabled = false
        }

        @JvmStatic
        val logger: Logger by lazy {
            Services.singleton(Logger::class.java, this::class.java.classLoader)
        }

        @JvmStatic
        fun println(vararg message: String) {
            println(logger, *message)
        }

        @JvmStatic
        fun println(logger: Logger, vararg message: String) {
            message.isEmpty() && return
            logger.println(*message)
        }

        @JvmStatic
        fun debug(vararg message: String) {
            debug(logger, *message)
        }

        @JvmStatic
        fun debug(logger: Logger, vararg message: String) {
            message.isEmpty() && return
            debugIsEnabled || return
            logger.println(*message.map {
                "${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.DEBUG.colorCode} $it"
            }.toTypedArray())
        }

        @JvmStatic
        fun info(vararg message: String) {
            info(logger, *message)
        }

        @JvmStatic
        fun info(logger: Logger, vararg message: String) {
            message.isEmpty() && return
            logger.println(*message.map {
                "${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.INFO.colorCode} $it"
            }.toTypedArray())
        }

        @JvmStatic
        fun warn(vararg message: String) {
            warn(logger, *message)
        }

        @JvmStatic
        fun warn(logger: Logger, vararg message: String) {
            message.isEmpty() && return
            logger.println(*message.map {
                "${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.WARN.colorCode} $it"
            }.toTypedArray())
        }

        @JvmStatic
        fun error(vararg message: String) {
            error(logger, *message)
        }

        @JvmStatic
        fun error(logger: Logger, vararg message: String) {
            message.isEmpty() && return
            logger.println(*message.map {
                "${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.ERROR.colorCode} $it"
            }.toTypedArray())
        }

        @JvmStatic
        fun error(vararg throwable: Throwable) {
            error(logger, *throwable)
        }

        @JvmStatic
        fun error(logger: Logger, vararg throwable: Throwable) {
            throwable.isEmpty() && return
            logger.println(*throwable.map {
                val stackTrace = StringWriter(256)
                PrintWriter(stackTrace).use { writer ->
                    it.printStackTrace(writer)
                }
                return@map "${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.DEBUG.colorCode} $stackTrace"
            }.toTypedArray())
        }
    }

    fun println(vararg message: String)

    fun debug(vararg message: String) {
        debug(this, *message)
    }

    fun info(vararg message: String) {
        info(this, *message)
    }

    fun warn(vararg message: String) {
        warn(this, *message)
    }

    fun error(vararg message: String) {
        error(this, *message)
    }
}