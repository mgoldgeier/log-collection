package mgoldgeier.rest

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Produces

@Controller("/logs")
class LogController {
    @Get("/{file}/events")
    @Produces(MediaType.TEXT_PLAIN)
    fun getEvents(@PathVariable file: String): String {
        TODO()
    }
}
