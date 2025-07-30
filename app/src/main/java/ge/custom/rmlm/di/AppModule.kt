package ge.custom.rmlm.di

import ge.custom.rmlm.data.repository.RecordingsRepositoryImpl
import ge.custom.rmlm.domain.repository.RecordingsRepository
import ge.custom.rmlm.domain.usecase.DeleteRecordingUseCase
import ge.custom.rmlm.domain.usecase.LoadRecordingsUseCase
import ge.custom.rmlm.presenatation.mapper.RecordingMapper
import ge.custom.rmlm.presenatation.viewmodels.RecordViewModel
import ge.custom.rmlm.presenatation.viewmodels.RecordingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    includes(sharedModule)

    single { androidContext().contentResolver }
    single<RecordingsRepository> { RecordingsRepositoryImpl(get(), get()) }

    single {
        LoadRecordingsUseCase(
            get(),
            get(named(RecordingErrorsSuspendRunner))
        )
    }
    single {
        DeleteRecordingUseCase(
            get(),
            get(named(DefaultSuspendRunner))
        )
    }
    single { RecordingMapper() }
    viewModel { RecordViewModel(get()) }
    viewModel { RecordingsViewModel(get(), get(), get()) }
}
