package ge.custom.rmlm.data.suspendrunners

import ge.custom.rmlm.common.Result
import ge.custom.rmlm.data.suspendrunners.errorhandlers.ErrorHandler
import ge.custom.rmlm.domain.usecase.base.SuspendFunction
import ge.custom.rmlm.domain.usecase.base.SuspendRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class DefaultSuspendRunner(
    private val dispatcher: CoroutineDispatcher,
    private val errorHandler: ErrorHandler
) : SuspendRunner {
    override suspend fun <R> invoke(execute: SuspendFunction<R>) =
        try {
            withContext(dispatcher) {

                Result.Success(execute())
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            // or Result.Error(errorHandler.handleException(e))
            errorHandler.handleError(e)
        }
}