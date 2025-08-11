package ge.custom.rmlm.di

import ge.custom.rmlm.data.repository.MediaStoreParamsProvider
import ge.custom.rmlm.data.suspendrunners.DefaultSuspendRunner
import ge.custom.rmlm.data.suspendrunners.errorhandlers.DefaultErrorHandler
import ge.custom.rmlm.data.suspendrunners.errorhandlers.ErrorHandler
import ge.custom.rmlm.data.suspendrunners.errorhandlers.RecordingIOErrorsHandler
import ge.custom.rmlm.domain.usecase.base.SuspendRunner
import ge.custom.rmlm.presenatation.service.RecorderServiceState
import ge.custom.rmlm.presenatation.service.RecorderServiceStateImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sharedModule = module {
    single<CoroutineDispatcher>(named(IODispatcher)) {
        Dispatchers.IO
    }
    single<ErrorHandler>(named(DefaultErrorHandler)) {
        DefaultErrorHandler()
    }
    single<ErrorHandler>(named(RecordingIOErrorsHandler)) {
        RecordingIOErrorsHandler()
    }
    single<SuspendRunner>(named(DefaultSuspendRunner)) {
        DefaultSuspendRunner(
            get(
                named(
                    IODispatcher
                )
            ),
            get(
                named(
                    DefaultErrorHandler
                )
            )
        )
    }

    single<SuspendRunner>(named(RecordingIOErrorsSuspendRunner)) {
        DefaultSuspendRunner(
            get(
                named(
                    IODispatcher
                )
            ),
            get(
                named(
                    RecordingIOErrorsHandler
                )
            )
        )
    }
    single<RecorderServiceState> { RecorderServiceStateImpl() }
    single { MediaStoreParamsProvider() }
}

const val DefaultSuspendRunner = "DefaultSuspendRunner"
const val RecordingIOErrorsSuspendRunner = "RecordingIOErrorsSuspendRunner"
const val IODispatcher = "IODispatcher"
const val DefaultErrorHandler = "DefaultErrorHandler"
private const val RecordingIOErrorsHandler = "RecordingIOErrorHandler"