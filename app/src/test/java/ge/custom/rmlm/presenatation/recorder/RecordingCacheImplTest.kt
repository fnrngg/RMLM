package ge.custom.rmlm.presenatation.recorder

import ge.custom.rmlm.common.Result
import ge.custom.rmlm.domain.usecase.SaveRecordingUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test

class RecordingCacheImplTest {
    private val onesAndTwo = ByteArray(2) { 1 } + ByteArray(1) { 2 }
    private lateinit var tempBytes: ByteArray
    private lateinit var recordingCache: RecordingCache
    private lateinit var saveRecordingUseCase: SaveRecordingUseCase
    private var currPosition = 0L

    private fun setup() {
        saveRecordingUseCase = mockk {
            coEvery { this@mockk(any()) } returns Result.Success(Unit)
        }
        recordingCache = RecordingCacheImpl(
            "",
            mockk {
                every { getFile(any(), any()) } returns File("")
                every { getRandomAccessFile(any(), any()) } returns mockk {
                    every { write(any<ByteArray>()) } answers {
                        firstArg<ByteArray>().forEach {
                            tempBytes[currPosition.toInt()] = it
                            currPosition += 1
                        }
                    }
                    every { seek(any()) } answers {
                        currPosition = firstArg()
                    }
                }
            },
            saveRecordingUseCase
        )
        recordingCache.initCache()
    }

    @Test
    fun `when total passed bytes are more than recordSize then resets from beginning for extra bytes`() = runTest {
        tempBytes = ByteArray(5)
        setup()
        recordingCache.saveInCache(onesAndTwo, readBytesCount = 3, recordingSize = 5)
        recordingCache.saveInCache(onesAndTwo, readBytesCount = 3, recordingSize = 5)
        assert(tempBytes.contentEquals(byteArrayOf(2, 1, 2, 1, 1)))

    }

    @Test
    fun `when readBytesCount is less than recordSize then sets values in order`() = runTest {
        tempBytes = ByteArray(5)
        setup()
        recordingCache.saveInCache(onesAndTwo, readBytesCount = 3, recordingSize = 5)
        assert(tempBytes.contentEquals(byteArrayOf(1, 1, 2, 0, 0)))
    }

    @Test
    fun `when readBytesCount is more than recordSize then resets from beginning for extra bytes`() = runTest {
        tempBytes = ByteArray(2)
        setup()
        recordingCache.saveInCache(onesAndTwo, readBytesCount = 3, recordingSize = 2)
        assert(tempBytes.contentEquals(byteArrayOf(2, 1)))
    }

    @Test
    fun `when saveRecording then call saveRecordingUseCase`() = runTest {
        tempBytes = ByteArray(1)
        setup()
        recordingCache.saveRecording(restart = false, minBufferSize = 1)
        coVerify { saveRecordingUseCase(any()) }
    }
}