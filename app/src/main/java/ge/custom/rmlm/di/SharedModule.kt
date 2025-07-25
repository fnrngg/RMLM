package ge.custom.rmlm.di

import ge.custom.rmlm.data.suspendrunners.DefaultSuspendRunner
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
    single<SuspendRunner>(named(DefaultSuspendRunner)) {
        DefaultSuspendRunner(get(named(IODispatcher)))
    }
    single<RecorderServiceState> { RecorderServiceStateImpl() }
}

const val DefaultSuspendRunner = "DefaultSuspendRunner"
const val IODispatcher = "IODispatcher"