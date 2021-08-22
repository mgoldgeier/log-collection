package mgoldgeier.reader

import io.micronaut.context.annotation.Property
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implements a basic log reader that can tail a specified file and filter text.
 *
 * Basic algorithm as follows:
 * - Seek to the end of the file and read last block of data equal to specified buffer size.
 * - Search over buffer in reverse looking for new line byte. (This algorithm could be modified for
 *   multi-byte new lines.)
 * - When new line is located, process the bytes on that line, assuming UTF-8 encoding.
 * - Add line text to list, matching on filter if one was specified.
 * - If more lines are needed beyond buffer, read back another block of the same size from the last
 *   known new line.
 * - If no new line was found in current buffer, the line is too large to fit in our buffer; break it
 *   up into multiple events and process each separately. Filter text split in half will no be matched in
 *   this case.
 *
 * Assumptions/Limitations:
 * - UTF-8 log file encoding
 * - New line designated by 0x0A single byte ('\n')
 * - Event cannot exceed buffer size; when line text exceeds this size, it is broken up into multiple events.
 */
@Singleton
class LogReader @Inject constructor(
    @Property(name = "base-path") private val basePath: Path,
    @Property(name = "buffer-size", defaultValue = "8096") private val bufferSize: Int
) {
    companion object {
        const val NEW_LINE: Byte = 0x0A
    }

    /**
     * Matches events in a log file, starting at the end.
     * @param name The log file to read
     * @param num The number of events to match on
     * @param filter Optional case-insensitive filter text; when null, will match every line
     * @return List of matching event lines; sorted with most recent first
     */
    fun tail(name: String, num: Int, filter: String? = null): List<String> {
        val buffer = ByteBuffer.allocate(bufferSize)
        val matching = mutableListOf<String>()

        RandomAccessFile(resolvePath(name).toFile(), "r").use { file ->
            val channel = file.channel
            var endPosition = channel.size()

            while (endPosition > 0) {
                val startPosition = (endPosition - bufferSize).coerceAtLeast(0)
                buffer.limit((endPosition - startPosition).toInt())

                val bytesRead = channel.read(buffer, startPosition)
                buffer.flip()

                for (i in buffer.limit() - 1 downTo 0) {
                    val byte = buffer.get(i)
                    if (byte == NEW_LINE) {
                        // If we found a new line, extract the text between the new line and limit
                        buffer.position(i + 1)
                        processLine(buffer, filter)?.let { matching.add(it) }
                        if (matching.size == num) return matching.toList()

                        // Set limit to the new line
                        buffer.limit(i)
                    }
                }

                if (buffer.position() == 0) {
                    // If we didn't find any new lines in the buffer, that means that the log entry is too large for
                    // our buffer; for now, we'll just divide up the entry into entries of the max possible size
                    processLine(buffer, filter)?.let { matching.add(it) }
                    if (matching.size == num) return matching.toList()
                    endPosition -= bytesRead
                } else {
                    endPosition -= bytesRead - buffer.position()
                }

                buffer.clear()
            }
        }

        return matching.toList()
    }

    private fun resolvePath(name: String) = basePath.resolve(name)

    private fun processLine(buffer: ByteBuffer, filter: String?) = StandardCharsets.UTF_8.decode(buffer).toString()
        .let { text -> if (filter == null || text.contains(filter, true)) text else null }
}
