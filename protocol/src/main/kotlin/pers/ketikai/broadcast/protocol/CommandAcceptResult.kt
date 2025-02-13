package pers.ketikai.broadcast.protocol

import pers.ketikai.broadcast.common.LogLevel
import pers.ketikai.broadcast.common.Logger

sealed class CommandAcceptResult(
    val success: Boolean,
    private val logLevel: LogLevel?,
    private val message: String?,
    var executor: CommandExecutor? = null
) {
    private class Success(
        message: String? = null
    ): CommandAcceptResult(true, LogLevel.INFO, message)

    private class Failure(
        message: String? = null
    ): CommandAcceptResult(false, LogLevel.ERROR, message)

    private class PermissionDenied(
        permission: String? = null
    ): CommandAcceptResult(false, LogLevel.ERROR, "Permission Denied${if (permission != null) ": $permission" else ""}")

    companion object {

        @JvmStatic
        val SUCCESS: CommandAcceptResult = Success()

        @JvmStatic
        val FAILURE: CommandAcceptResult = Failure()

        @JvmStatic
        val PERMISSION_DENIED: CommandAcceptResult = PermissionDenied()

        @JvmStatic
        fun auto(success: Boolean, message: String? = null): CommandAcceptResult {
            return if (success) Success(message) else Failure(message)
        }

        @JvmStatic
        fun success(message: String? = null): CommandAcceptResult {
            message == null && return SUCCESS
            return Success(message)
        }

        @JvmStatic
        fun failure(message: String? = null): CommandAcceptResult {
            message == null && return FAILURE
            return Failure(message)
        }

        @JvmStatic
        fun permissionDenied(permission: String? = null): CommandAcceptResult {
            permission == null && return PERMISSION_DENIED
            return PermissionDenied(permission)
        }
    }


    fun execute(context: Map<String, Any?>, sender: BroadcastSender, vararg arguments: String): Boolean {
        var ret: Boolean? = null
        if (success) {
            ret = executor?.execute(context, sender, *arguments)
        }
        return ret ?: log(sender.logger)
    }

    private fun log(logger: Logger): Boolean {
        val message = message ?: return success
        val level = logLevel ?: LogLevel.UNKNOWN
        level.log(logger, message)
        return success
    }
}