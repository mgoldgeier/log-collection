package mgoldgeier.rest

import io.micronaut.http.HttpStatus.NOT_FOUND
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import mgoldgeier.reader.LogReader
import java.nio.file.NoSuchFileException
import javax.inject.Inject

@Controller("/logs")
class LogController @Inject constructor(
    private val logReader: LogReader
) {
    @Get("/{name:[0-9a-zA-Z_\\-. ]+}/events")
    @Produces(MediaType.TEXT_PLAIN)
    fun getEvents(
        @PathVariable name: String,
        @QueryValue("n") numLines: Int,
        @QueryValue("filter") filter: String?
    ): String {
        return try {
            logReader.tail(name, numLines, filter).joinToString("\n")
        } catch (e: NoSuchFileException) {
            throw HttpStatusException(NOT_FOUND, "File not found")
        }
    }
}
