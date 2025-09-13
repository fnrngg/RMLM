package ge.custom.rmlm.presenatation.recorder.cache

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import ge.custom.rmlm.data.repository.headergenerator.AudioHeaderGenerator
import ge.custom.rmlm.domain.usecase.SaveInMemoryRecordingUseCase
import ge.custom.rmlm.presenatation.recorder.RecorderImpl.Companion.SAMPLE_RATE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RecordingInMemoryCache(
    private val saveInMemoryRecordingUseCase: SaveInMemoryRecordingUseCase,
    private val aacHeaderGenerator: AudioHeaderGenerator
) : RecordingCache {

    private val bufferMutex = Mutex()
    private var isBufferFull: Boolean = false

    // offset in seconds array
    private var currentSecond = 0

    // offset in bytes of current second
    private var currentSecondOffset = 0

    // offset in bytes of current second in pcm data
    private var currentSecondRawOffset = 0

    private var mediaCodec: MediaCodec? = null

    private lateinit var bytesBySeconds: Array<ByteArray>

    private var res = byteArrayOf()

    private var job: Job? = null

    override fun initCache(duration: Int) {
        bytesBySeconds = Array(1 * 60) { byteArrayOf() }
        val mediaFormat = MediaFormat.createAudioFormat(
            MediaFormat.MIMETYPE_AUDIO_AAC,
            SAMPLE_RATE,
            1
        )
        mediaFormat.setInteger(
            MediaFormat.KEY_AAC_PROFILE,
            MediaCodecInfo.CodecProfileLevel.AACObjectLC
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 705600)
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 16384)


        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
        mediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mediaCodec?.start()
        mediaCodec?.let { encoder ->
            job = CoroutineScope(Dispatchers.IO).launch {
                handleOutput(encoder)
            }
        }
    }

    private var presentationTimeUs: Long = 0
    private fun calculatePresentationTime(
        bufferSize: Int
    ): Long {
        val numSamples = bufferSize / 2
        presentationTimeUs += (numSamples * 1_000_000L) / SAMPLE_RATE
        return presentationTimeUs
    }

    private fun handleOutput(encoder: MediaCodec) {
        val bufferInfo = MediaCodec.BufferInfo()
        while (true) {
            val outputBufferIndex = encoder.dequeueOutputBuffer(
                bufferInfo,
                TIMEOUT_US
            ) // Use a longer timeout when waiting for EOS

            when (outputBufferIndex) {
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    // Handle format change if necessary (e.g., get new format from encoder.outputFormat)
                    Log.i("MediaCodec", "Output format changed: ${encoder.outputFormat}")
                }

                MediaCodec.INFO_TRY_AGAIN_LATER -> {
//                    if (!waitForEOS) { // If not expecting EOS, and no output, break
//                        break
//                    }
                    // If waiting for EOS, continue polling
                }

                else -> {
                    if (outputBufferIndex >= 0) {
                        val outputBuffer = encoder.getOutputBuffer(outputBufferIndex)
                        outputBuffer?.let {
                            if (bufferInfo.size > 0) { // Process data only if there is some
                                val encodedData = ByteArray(bufferInfo.size)
                                it.get(encodedData)
                                // outPutStream.write(encodedData) // Uncomment and use your stream
                                // Handle res, bytesBySeconds etc.

                                val currData = aacHeaderGenerator.getEncodedHeader(
                                    encodedData.size + 7,
                                    SAMPLE_RATE,
                                    1
                                ) + encodedData

                                bytesBySeconds[currentSecond] = if (currentSecondOffset > 0) {
                                    bytesBySeconds[currentSecond] + currData
                                } else {
                                    currData
                                }
                                currentSecondOffset += encodedData.size + 7

//                                res += aacHeaderGenerator.getEncodedHeader(
//                                    encodedData.size + 7,
//                                    SAMPLE_RATE,
//                                    1
//                                )
//                                res += encodedData
                            }
                            encoder.releaseOutputBuffer(outputBufferIndex, false)

                        }
                        if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                            return // Exit drainEncoder function
                        }
                    }
                }
            }
//            if (!waitForEOS && outputBufferIndex < 0) { // If not waiting for EOS and no output, break the loop
//                break
//            }
        }
    }

    private fun encodeAndSave(bytes: ByteArray) {
        mediaCodec?.let { encoder ->
            var inputOffset = 0
            var eos = false
            var i =0
            while (!eos) {

                i++
                val inputBufferIndex = encoder.dequeueInputBuffer(TIMEOUT_US)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = encoder.getInputBuffer(inputBufferIndex)
                    inputBuffer?.let { buffer ->
                        buffer.clear()
                        val remaining = bytes.size - inputOffset
                        val inputSize = minOf(buffer.capacity(), remaining)
                        if (inputSize > 0) {
                            buffer.put(bytes, inputOffset, inputSize)
                            inputOffset += inputSize

                            if (inputOffset >= bytes.size) {
                                eos = true
                            }
                            encoder.queueInputBuffer(
                                inputBufferIndex,
                                0,
                                inputSize,
                                calculatePresentationTime(inputSize),
                                0
                            )
                        }
                    }
                }
            }
        }
    }

    override suspend fun saveInCache(
        tempBytes: ByteArray,
        readBytesCount: Int,
        recordingSize: Int
    ) {
        bufferMutex.withLock {
            Log.d("RecordingInMemoryCache", "saveInCache: ${currentSecondRawOffset + readBytesCount}")
            when (currentSecondRawOffset + readBytesCount) {
                RAW_RECORDING_SECOND_SIZE -> {
                    encodeAndSave(
                        tempBytes
                    )
                    currentSecondOffset = 0
                    currentSecond = (currentSecond + 1) % bytesBySeconds.size
                    if (currentSecond == 0) {
                        isBufferFull = true
                    }
                    currentSecondRawOffset = 0
                    bytesBySeconds[currentSecond] = byteArrayOf()
                }

                in 0.until(RAW_RECORDING_SECOND_SIZE) -> {
                    encodeAndSave(
                        tempBytes
                    )
                    currentSecondRawOffset += readBytesCount
                }

                else -> {
                    val cutPosition = RAW_RECORDING_SECOND_SIZE - currentSecondRawOffset
                    encodeAndSave(
                        tempBytes.copyOfRange(
                            0,
                            cutPosition
                        )
                    )
                    currentSecondOffset = 0
                    currentSecond = (currentSecond + 1) % bytesBySeconds.size
                    if (currentSecond == 0) {
                        isBufferFull = true
                    }
                    encodeAndSave(
                        tempBytes.copyOfRange(
                            cutPosition,
                            readBytesCount
                        )
                    )
                    currentSecondRawOffset = readBytesCount - cutPosition
                }
            }
        }
    }

    override suspend fun saveRecording(restart: Boolean, minBufferSize: Int) {
        val resArray = bufferMutex.withLock {
            val totalSeconds = bytesBySeconds.size
            var resArray = byteArrayOf()
            if (isBufferFull) {
                for (i in 0.until(totalSeconds)) {
                    resArray += bytesBySeconds[(i + currentSecond) % totalSeconds]
                }
            } else {
                for (i in 0..currentSecond) {
                    resArray += bytesBySeconds[i]
                }
            }
            currentSecond = 0
            currentSecondOffset = 0
            currentSecondRawOffset = 0
            isBufferFull = false
            resArray
        }

        mediaCodec?.let { encoder ->
            val inputBufferIndex = encoder.dequeueInputBuffer(TIMEOUT_US)
            encoder.queueInputBuffer(
                inputBufferIndex,
                0,
                0,
                0,
                MediaCodec.BUFFER_FLAG_END_OF_STREAM
            )
            job?.cancel()
        }

        saveInMemoryRecordingUseCase(resArray).also {
            Log.d("RecordingInMemoryCache", "saveInMemoryRecordingUseCase: $it")
        }
    }

    override suspend fun stopCaching(deleteCache: Boolean) {
        bufferMutex.withLock {
            if (deleteCache) {
                bytesBySeconds = emptyArray()
            }
            currentSecond = 0
            currentSecondRawOffset = 0
            currentSecondOffset = 0
            isBufferFull = false
            mediaCodec?.stop()
            mediaCodec?.release()
            mediaCodec = null
        }
    }

    companion object {
        private const val RAW_RECORDING_SECOND_SIZE = 88200
        private const val TIMEOUT_US = 100000L // Use Long for timeouts

    }
}