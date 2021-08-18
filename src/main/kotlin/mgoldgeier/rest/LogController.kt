package mgoldgeier.rest

import com.fasterxml.jackson.annotation.JsonInclude
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.HttpStatus.NOT_FOUND
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.exceptions.HttpStatusException
import mgoldgeier.reader.LogReader
import java.net.URL
import java.nio.file.NoSuchFileException
import javax.inject.Inject

@Controller("/logs/{name:[0-9a-zA-Z_\\-. ]+}")
class LogController @Inject constructor(
    private val logReader: LogReader
) {
    @Get("/events")
    @Produces(MediaType.APPLICATION_JSON)
    fun getEvents(
        @PathVariable name: String,
        @QueryValue("num") num: Int,
        @QueryValue("filter") filter: String?
    ): Events {
        return try {
            Events(logReader.tail(name, num, filter))
        } catch (e: NoSuchFileException) {
            throw HttpStatusException(NOT_FOUND, "File not found")
        }
    }

    @Get("/aggregates")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAggregateEvents(
        @PathVariable name: String,
        @QueryValue("num") num: Int,
        @QueryValue("filter") filter: String?,
        @QueryValue("remotes") remotes: List<String>
    ): AggregateEvents {
        return try {
            remotes
                .distinct()
                .associateWith { LogClient(URL(it)).getEvents(name, num, filter) }
                .mapValues { it.value.blockingFirst() }
                .let { AggregateEvents(it) }
        } catch (e: HttpClientException) {
            throw HttpStatusException(BAD_REQUEST, "Cannot connect to remote")
        }
    }
}

@JsonInclude(JsonInclude.Include.ALWAYS)
data class Events(val events: List<String>)

@JsonInclude(JsonInclude.Include.ALWAYS)
data class AggregateEvents(val aggregates: Map<String, Events>)
