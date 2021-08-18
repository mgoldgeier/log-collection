package mgoldgeier.reader

import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
@Property(name = "base-path", value = "src/test/resources/env1")
internal class LogReaderTest @Inject constructor(
    private val logReader: LogReader
) {
    @Test
    fun `unfiltered log retrieval`() {
        val events = logReader.tail("sample1.log", 3)

        assertEquals(3, events.size) { "found 3 matching events" }
        assertEquals(listOf(
            "<130>Aug 16 06:00:34 abshire7814 aut[6251]: The COM panel is down, navigate the mobile application so we can calculate the EXE firewall!",
            "<9>Aug 16 06:00:34 kunde6612 aut[8188]: If we input the port, we can get to the SDD port through the auxiliary JBOD matrix!",
            "<50>Aug 16 06:00:34 raynor8428 dicta[4631]: Use the open-source AI application, then you can connect the neural array!"
        ), events)
    }

    @Test
    fun `filtered log retrieval`() {
        val events = logReader.tail("sample1.log", 5, "pixel")

        assertEquals(5, events.size) { "found 5 matching events" }
        assertEquals("<88>Aug 16 06:00:34 bednar7007 quos[5398]: The COM pixel is down, copy the primary feed so we can input the EXE array!",
            events.first()) { "got last matching event as first item" }
        assertTrue(events.all { it.contains("pixel", true) }) { "all events matched filter" }
    }

    @Test
    fun `fewer lines match than requested`() {
        val events = logReader.tail("sample1.log", 5, "miller4743")

        assertEquals(1, events.size) { "found 1 matching event" }
    }
}
