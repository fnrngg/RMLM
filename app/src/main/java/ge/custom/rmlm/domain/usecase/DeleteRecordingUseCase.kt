package ge.custom.rmlm.domain.usecase

import android.net.Uri
import ge.custom.rmlm.domain.repository.RecordingsRepository
import ge.custom.rmlm.domain.usecase.base.UseCase
import ge.custom.rmlm.domain.usecase.base.SuspendRunner

class DeleteRecordingUseCase(
    private val repository: RecordingsRepository,
    suspendRunner: SuspendRunner
) : UseCase<Uri, Unit>(suspendRunner) {
    override suspend fun execute(params: Uri) {
        repository.deleteRecording(params)
    }
}