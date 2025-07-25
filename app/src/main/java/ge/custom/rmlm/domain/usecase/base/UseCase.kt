package ge.custom.rmlm.domain.usecase.base

import ge.custom.rmlm.common.Result


abstract class UseCase<in P, out R>(private val suspendRunner: SuspendRunner) {

    open suspend operator fun invoke(params: P): Result<R> {
        return suspendRunner {
            execute(params)
        }
    }

    protected abstract suspend fun execute(params: P): R
}