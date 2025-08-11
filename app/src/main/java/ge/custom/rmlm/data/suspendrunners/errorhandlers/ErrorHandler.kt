package ge.custom.rmlm.data.suspendrunners.errorhandlers

import ge.custom.rmlm.common.Result

interface ErrorHandler {
    suspend fun handleError(error: Exception): Result.Error
}