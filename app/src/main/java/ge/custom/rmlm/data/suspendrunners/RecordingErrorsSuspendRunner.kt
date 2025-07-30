package ge.custom.rmlm.data.suspendrunners

import ge.custom.rmlm.common.Result
import ge.custom.rmlm.domain.usecase.base.SuspendFunction
import ge.custom.rmlm.domain.usecase.base.SuspendRunner
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RecordingErrorsSuspendRunner(
    private val dispatcher: CoroutineDispatcher
) : SuspendRunner {

    override suspend fun <R> invoke(execute: SuspendFunction<R>): Result<R> {
        return try {
            withContext(dispatcher) {
                Result.Success(execute())
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: SecurityException) {
            Result.Error(PermissionDeniedException())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

class PermissionDeniedException : Exception()