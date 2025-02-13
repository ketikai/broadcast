package pers.ketikai.broadcast.common

enum class LogLevel(val colorCode: String) {

    UNKNOWN(""),
    DEBUG("§f"),
    INFO("§a"),
    WARN("§e"),
    ERROR("§c");

    fun log(vararg message: String) {
        when (this) {
            DEBUG -> Logger.debug(*message)
            INFO -> Logger.info(*message)
            WARN -> Logger.warn(*message)
            ERROR -> Logger.error(*message)
            else -> Logger.println(*message)
        }
    }

    fun log(logger: Logger, vararg message: String) {
        when (this) {
            DEBUG -> logger.debug( *message)
            INFO -> logger.info( *message)
            WARN -> logger.warn( *message)
            ERROR -> logger.error( *message)
            else -> logger.println( *message)
        }
    }
}