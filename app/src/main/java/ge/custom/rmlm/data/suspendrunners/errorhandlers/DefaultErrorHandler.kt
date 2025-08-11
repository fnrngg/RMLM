package ge.custom.rmlm.data.suspendrunners.errorhandlers

import ge.custom.rmlm.common.Result

class DefaultErrorHandler: ErrorHandler {
    override suspend fun handleError(error: Exception): Result.Error {
        return Result.Error(error)
    }
}