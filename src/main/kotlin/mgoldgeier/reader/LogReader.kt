package mgoldgeier.reader

import io.micronaut.context.annotation.Property
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.path.useLines

// TODO: May want to implement custom line reader that can cap line length

@Singleton
class LogReader @Inject constructor(
    @Property(name = "base-path") private val basePath: Path
) {
    fun tail(name: String, num: Int, filter: String? = null): List<String> {
        val matching = mutableListOf<String>()
        val file = resolvePath(name)

        file.useLines { lines ->
            for (line in lines) {
                if ((filter != null && line.contains(filter, true)) || filter == null) {
                    matching.add(0, line)
                }
                if (matching.size > num) {
                    matching.removeLast()
                }
            }
        }

        return matching.toList()
    }

    private fun resolvePath(name: String) = basePath.resolve(name)
}
