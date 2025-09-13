package ge.custom.rmlm.data.repository.headergenerator
interface AudioHeaderGenerator {
    fun getEncodedHeader(pcmDataSize: Int, sampleRate: Int, channels: Int): ByteArray
}