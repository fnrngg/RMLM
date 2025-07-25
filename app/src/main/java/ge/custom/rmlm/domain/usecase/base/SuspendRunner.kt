package ge.custom.rmlm.domain.usecase.base

import ge.custom.rmlm.common.Result

interface SuspendRunner {
    suspend operator fun <R> invoke(execute: SuspendFunction<R>): Result<R>
}

typealias SuspendFunction<R> = suspend () -> R