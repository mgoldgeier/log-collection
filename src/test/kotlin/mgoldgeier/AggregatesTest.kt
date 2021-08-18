package mgoldgeier

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.uri.UriBuilder
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import mgoldgeier.rest.AggregateEvents
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@MicronautTest
class AggregatesTest {
    @Test
    fun `aggregates returned from multiple hosts`() {
        val props1 = mapOf("base-path" to "src/test/resources/env1")
        val props2 = mapOf("base-path" to "src/test/resources/env2")

        ApplicationContext.run(EmbeddedServer::class.java, props1).use { server1 ->
            ApplicationContext.run(EmbeddedServer::class.java, props2).use { server2 ->
                val client = server1.applicationContext.createBean(RxHttpClient::class.java, server1.url)
                val remotes = listOf(server1.url.toString(), server2.url.toString())
                val uri = UriBuilder.of("/logs/sample1.log/aggregates")
                    .queryParam("num", 5)
                    .queryParam("filter", "pixel")
                    .queryParam("remotes", *remotes.toTypedArray())
                    .build()
                val response = client.toBlocking().exchange(uri.toString(), AggregateEvents::class.java)

                assertEquals(HttpStatus.OK, response.status)
                assertTrue(response.body.isPresent)

                val aggregateEvents = response.body.get()
                assertIterableEquals(remotes, aggregateEvents.aggregates.keys)
                assertTrue(aggregateEvents.aggregates.all { it.value.events.size == 5 })
                assertTrue(aggregateEvents.aggregates.all { it.value.events.all { it.contains("pixel") } })
            }
        }
    }
}
