package mgoldgeier.reader

import java.nio.file.Path
import kotlin.io.path.useLines

// TODO: May want to implement custom line reader that can cap line length

class LogReader(private val file: Path) {
    fun tail(numLines: Int, filter: String? = null): List<String> {
        val matching = mutableListOf<String>()

        file.useLines { lines ->
            for (line in lines) {
                if ((filter != null && line.contains(filter, true)) || filter == null) {
                    matching.add(0, line)
                }
                if (matching.size > numLines) {
                    matching.removeLast()
                }
            }
        }

        return matching.toList()
    }
}
