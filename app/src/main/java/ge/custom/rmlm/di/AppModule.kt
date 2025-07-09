package ge.custom.rmlm.di

import ge.custom.rmlm.data.suspendrunners.DefaultSuspendRunner
import ge.custom.rmlm.domain.usecase.base.SuspendRunner
import ge.custom.rmlm.presenatation.viewmodels.RecordViewModel
import ge.custom.rmlm.presenatation.viewmodels.RecordingsViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single<CoroutineDispatcher>(named(IODispatcher)) {
        Dispatchers.IO
    }
    single<SuspendRunner>(named(DefaultSuspendRunner)) {
        DefaultSuspendRunner(get(named(IODispatcher)))
    }

    viewModel { RecordViewModel() }
    viewModel { RecordingsViewModel() }
}

const val DefaultSuspendRunner = "DefaultSuspendRunner"
const val IODispatcher = "IODispatcher"