package ge.custom.rmlm.presenatation.recorder

import java.io.File
import java.io.RandomAccessFile

class TemporaryFileProvider {
    fun getFile(directoryPath: String, filename: String) =
        File(directoryPath, filename)

    fun getRandomAccessFile(file: File, mode: String) = RandomAccessFile(file, mode)
}