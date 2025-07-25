package ge.custom.rmlm.data.repository

import ge.custom.rmlm.data.repository.RecordRepositoryImpl.Companion.CHANNEL_COUNT

class WAVHeaderGenerator : AudioHeaderGenerator {
    override fun getEncodedHeader(
        pcmDataSize: Long,
        sampleRate: Int,
        channels: Int
    ): ByteArray {
        val totalDataLen = pcmDataSize + 36
        val header = ByteArray(44)

        val channelsShort = CHANNEL_COUNT
        val byteRateTotal = sampleRate * channelsShort * 2

        // RIFF/WAVE header
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        writeInt(header, 4, totalDataLen.toInt()) // file size - 8
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        writeInt(header, 16, 16) // Subchunk1Size (16 for PCM)
        writeShort(header, 20, 1.toShort()) // AudioFormat (1 for PCM)
        writeShort(header, 22, channelsShort.toShort())
        writeInt(header, 24, sampleRate)
        writeInt(header, 28, byteRateTotal)
        writeShort(header, 32, (channelsShort * 2).toShort()) // Block align
        writeShort(header, 34, 16.toShort()) // Bits per sample
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        writeInt(header, 40, pcmDataSize.toInt())

        return header
    }

    private fun writeInt(header: ByteArray, offset: Int, value: Int) {
        header[offset] = (value and 0xff).toByte()
        header[offset + 1] = ((value shr 8) and 0xff).toByte()
        header[offset + 2] = ((value shr 16) and 0xff).toByte()
        header[offset + 3] = ((value shr 24) and 0xff).toByte()
    }

    private fun writeShort(header: ByteArray, offset: Int, value: Short) {
        header[offset] = (value.toInt() and 0xff).toByte()
        header[offset + 1] = ((value.toInt() shr 8) and 0xff).toByte()
    }
}

interface AudioHeaderGenerator {
    fun getEncodedHeader(pcmDataSize: Long, sampleRate: Int, channels: Int): ByteArray
}