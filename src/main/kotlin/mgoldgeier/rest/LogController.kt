package mgoldgeier.rest

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import mgoldgeier.reader.LogReader
import java.nio.file.Paths

@Controller("/logs")
class LogController {
    @Get("/{name}/events")
    @Produces(MediaType.TEXT_PLAIN)
    fun getEvents(
        @PathVariable name: String,
        @QueryValue("n") numLines: Int,
        @QueryValue("filter") filter: String?
    ): String {
        // TODO Need to sanitize log file name
        val file = Paths.get("/var/log", name)
        val reader = LogReader(file)

        return reader.tail(numLines, filter).joinToString("\n")
    }
}
