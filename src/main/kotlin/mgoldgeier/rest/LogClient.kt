package mgoldgeier.rest

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.uri.UriBuilder
import io.reactivex.Flowable
import java.net.URL

class LogClient(url: URL) {
    private val httpClient = RxHttpClient.create(url)

    fun getEvents(name: String, num: Int, filter: String?): Flowable<Events> =
        httpClient.retrieve(
            HttpRequest.GET<Events>(
                UriBuilder.of("/logs/{name}/events")
                    .queryParam("num", num)
                    .queryParam("filter", filter)
                    .expand(mutableMapOf("name" to name))
            ),
            Events::class.java
        )
}
