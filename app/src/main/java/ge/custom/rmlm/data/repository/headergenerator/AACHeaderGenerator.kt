package ge.custom.rmlm.data.repository.headergenerator

class AACHeaderGenerator : AudioHeaderGenerator {
    override fun getEncodedHeader(
        pcmDataSize: Int,
        sampleRate: Int,
        channels: Int
    ): ByteArray {
        val packet = ByteArray(7)

        val profile = 2 //AAC LC

        //39=MediaCodecInfo.CodecProfileLevel.AACObjectELD;
        val freqIdx = 4 //44.1KHz


        // fill in ADTS data
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF9.toByte()
        packet[2] = (((profile - 1) shl 6) + (freqIdx shl 2) + (channels shr 2)).toByte()
        packet[3] = (((channels and 3) shl 6) + (pcmDataSize shr 11)).toByte()
        packet[4] = ((pcmDataSize and 0x7FF) shr 3).toByte()
        packet[5] = (((pcmDataSize and 7) shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
        return packet
    }
}