package ge.custom.rmlm.data.suspendrunners.errorhandlers

import ge.custom.rmlm.common.Result

class RecordingIOErrorsHandler : ErrorHandler {
    override suspend fun handleError(error: Exception): Result.Error {
        return when (error) {
            // & if message is permissionDenied...
            is SecurityException -> {
                Result.Error(PermissionDeniedException())
            }
            // ...
            else -> {
                Result.Error(error)
            }
        }
    }
}

class PermissionDeniedException : Exception()