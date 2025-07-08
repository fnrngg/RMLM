package ge.custom.rmlm.di

import ge.custom.rmlm.data.suspendrunners.DefaultSuspendRunner
import ge.custom.rmlm.domain.usecase.base.SuspendRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val AppModule = module {
    single<CoroutineDispatcher>(named(IODispatcher)) {
        Dispatchers.IO
    }
    single<SuspendRunner>(named(DefaultSuspendRunner)) {
        DefaultSuspendRunner(get(named(IODispatcher)))
    }
}

const val DefaultSuspendRunner = "DefaultSuspendRunner"
const val IODispatcher = "IODispatcher"