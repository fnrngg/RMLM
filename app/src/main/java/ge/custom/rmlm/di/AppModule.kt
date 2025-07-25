package ge.custom.rmlm.di

import ge.custom.rmlm.data.repository.RecordingsRepositoryImpl
import ge.custom.rmlm.domain.repository.RecordingsRepository
import ge.custom.rmlm.domain.usecase.LoadRecordingsUseCase
import ge.custom.rmlm.presenatation.activity.MainActivityViewModel
import ge.custom.rmlm.presenatation.viewmodels.RecordViewModel
import ge.custom.rmlm.presenatation.viewmodels.RecordingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    includes(sharedModule)

    single { androidContext().contentResolver }
    single<RecordingsRepository> { RecordingsRepositoryImpl(get()) }

    single {
        LoadRecordingsUseCase(
            get(),
            get(named(DefaultSuspendRunner))
        )
    }
    viewModel { RecordViewModel(get()) }
    viewModel { RecordingsViewModel(get()) }
    viewModel { MainActivityViewModel() }
}
