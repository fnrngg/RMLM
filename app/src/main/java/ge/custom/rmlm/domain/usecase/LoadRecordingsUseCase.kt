package ge.custom.rmlm.domain.usecase

import ge.custom.rmlm.domain.model.RecordingData
import ge.custom.rmlm.domain.repository.RecordingsRepository
import ge.custom.rmlm.domain.usecase.base.SuspendRunner
import ge.custom.rmlm.domain.usecase.base.UseCase

class LoadRecordingsUseCase(
    private val recordingRepository: RecordingsRepository,
    suspendRunner: SuspendRunner
) : UseCase<String, List<RecordingData>>(suspendRunner) {
    override suspend fun execute(params: String): List<RecordingData> {
        return recordingRepository.getRecordings()
    }
}