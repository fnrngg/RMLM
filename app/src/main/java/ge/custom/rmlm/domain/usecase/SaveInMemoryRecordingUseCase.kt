package ge.custom.rmlm.domain.usecase

import ge.custom.rmlm.domain.repository.RecordRepository
import ge.custom.rmlm.domain.usecase.base.SuspendRunner
import ge.custom.rmlm.domain.usecase.base.UseCase

class SaveInMemoryRecordingUseCase(
    suspendRunner: SuspendRunner,
    private val recordRepository: RecordRepository,
) : UseCase<ByteArray, Unit>(suspendRunner) {

    override suspend fun execute(params: ByteArray) {
        recordRepository.saveRecordingAsAAC(params)
    }
}