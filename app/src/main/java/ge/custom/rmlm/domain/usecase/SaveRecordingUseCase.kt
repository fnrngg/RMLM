package ge.custom.rmlm.domain.usecase

import ge.custom.rmlm.domain.repository.RecordRepository
import ge.custom.rmlm.domain.usecase.base.SuspendRunner
import ge.custom.rmlm.domain.usecase.base.UseCase
import java.io.File

class SaveRecordingUseCase(
    suspendRunner: SuspendRunner,
    private val recordRepository: RecordRepository,
) : UseCase<SaveRecordingUseCaseParam, Unit>(suspendRunner) {

    override suspend fun execute(params: SaveRecordingUseCaseParam) {
        recordRepository.saveRecordingAsWAV(params.file, params.offset, params.bufferSize)
    }
}

data class SaveRecordingUseCaseParam(
    val file: File,
    val offset: Int,
    val bufferSize: Int
)