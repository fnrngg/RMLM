package ge.custom.rmlm.presenatation.recorder.cache


interface RecordingCache {

    fun initCache() {}
    fun initCache(duration: Int) {}

    suspend fun saveInCache(tempBytes: ByteArray, readBytesCount: Int, recordingSize: Int)

    suspend fun saveRecording(restart: Boolean, minBufferSize: Int)

    suspend fun stopCaching(deleteCache: Boolean)
}