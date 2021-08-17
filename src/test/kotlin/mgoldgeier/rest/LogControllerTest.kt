package mgoldgeier.rest

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
@Property(name = "base-path", value = "src/test/resources")
internal class LogControllerTest {
    @Inject
    @field:Client("/")
    lateinit var client: RxHttpClient

    @Test
    fun `non-existent file returns 404`() {
        val exception = assertThrows(HttpClientResponseException::class.java) {
            client.toBlocking().exchange<String>("/logs/missing/events?n=5")
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun `valid file returns 200`() {
        val response = client.toBlocking().exchange<String>("/logs/sample1.log/events?n=5&filter=pixel")

        assertEquals(HttpStatus.OK, response.status)
    }
}
