package mgoldgeier.rest

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
@Property(name = "base-path", value = "src/test/resources/env1")
internal class LogControllerTest {
    @Inject
    @field:Client("/")
    lateinit var client: RxHttpClient

    @Test
    fun `non-existent file returns 404`() {
        val exception = assertThrows(HttpClientResponseException::class.java) {
            client.toBlocking().exchange("/logs/missing/events?num=5", Events::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun `valid file returns 200`() {
        val response = client.toBlocking().exchange("/logs/sample1.log/events?num=5&filter=pixel", Events::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertTrue(response.body.isPresent)
        assertEquals(5, response.body.get().events.size)
        assertTrue(response.body.get().events.all { it.contains("pixel") })
    }
}
