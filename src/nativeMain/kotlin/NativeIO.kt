import kotlinx.cinterop.*
import platform.posix.*

// kotlin native doesn't have a nice function for reading file lines
fun lines(filePath: String) = sequence {
    val file = fopen(filePath, "r") ?: throw IllegalArgumentException("Cannot open input file")
    try {
        memScoped {
            val readBufferLength = 64 * 1024
            val buffer = allocArray<ByteVar>(readBufferLength)
            var line = fgets(buffer, readBufferLength, file)?.toKString()
            while (line != null) {
                yield(line)
                line = fgets(buffer, readBufferLength, file)?.toKString()
            }
        }
    } finally {
        fclose(file)
    }
}

fun now(): Long = memScoped {
    val timeVal = alloc<timeval>()
    gettimeofday(timeVal.ptr, null)
    (timeVal.tv_sec * 1000) + (timeVal.tv_usec / 1000)
}