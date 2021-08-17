package mgoldgeier.reader

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.io.path.toPath

internal class LogReaderTest {
    @Test
    fun `unfiltered log retrieval`() {
        val reader = LogReader(this::class.java.getResource("/sample1.log")!!.toURI().toPath())
        val events = reader.tail(3)

        assertEquals(3, events.size) { "found 3 matching events" }
        assertEquals(listOf(
            "<130>Aug 16 06:00:34 abshire7814 aut[6251]: The COM panel is down, navigate the mobile application so we can calculate the EXE firewall!",
            "<9>Aug 16 06:00:34 kunde6612 aut[8188]: If we input the port, we can get to the SDD port through the auxiliary JBOD matrix!",
            "<50>Aug 16 06:00:34 raynor8428 dicta[4631]: Use the open-source AI application, then you can connect the neural array!"
        ), events)
    }

    @Test
    fun `filtered log retrieval`() {
        val reader = LogReader(this::class.java.getResource("/sample1.log")!!.toURI().toPath())
        val events = reader.tail(5, "pixel")

        assertEquals(5, events.size) { "found 5 matching events" }
        assertEquals("<88>Aug 16 06:00:34 bednar7007 quos[5398]: The COM pixel is down, copy the primary feed so we can input the EXE array!",
            events.first()) { "got last matching event as first item" }
        assertTrue(events.all { it.contains("pixel", true) }) { "all events matched filter" }
    }

    @Test
    fun `fewer lines match than requested`() {
        val reader = LogReader(this::class.java.getResource("/sample1.log")!!.toURI().toPath())
        val events = reader.tail(5, "miller4743")

        assertEquals(1, events.size) { "found 1 matching event" }
    }

}
