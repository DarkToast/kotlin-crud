package de.tarent.crud.rest

import de.tarent.crud.adapters.rest.parseDateTime
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.SECONDS

internal class ParseDateTimeSpec {
    @Test
    fun `parses iso`() {
        val dateTime: LocalDateTime = parseDateTime("2022-12-29T13:55:56")
        assertThat(dateTime).isEqualTo(LocalDateTime.parse("2022-12-29T13:55:56").trim())
    }

    @Test
    fun `microseconds are unsupported`() {
        assertThatThrownBy { parseDateTime("2022-12-29T13:55:56.552639058") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("'2022-12-29T13:55:56.552639058' could not be parsed.")
    }

    @Test
    fun `zones are unsupported`() {
        assertThatThrownBy { parseDateTime("2022-12-29T13:55:56+01:00") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("'2022-12-29T13:55:56+01:00' could not be parsed.")
    }

    @Test
    fun `parse now`() {
        val dateTime = parseDateTime("now").trim()
        assertThat(dateTime).isCloseTo(now(), within(1, SECONDS))
    }

    @Test
    fun `parse now minus days`() {
        val dateTime = parseDateTime("now-4d").trim()
        assertThat(dateTime).isCloseTo(now().minusDays(4), within(1, SECONDS))
    }

    @Test
    fun `parse now plus days`() {
        val dateTime = parseDateTime("now+4d").trim()
        assertThat(dateTime).isCloseTo(now().plusDays(4), within(1, SECONDS))
    }

    @Test
    fun `parse now minus hours`() {
        val dateTime = parseDateTime("now-52h").trim()
        assertThat(dateTime).isCloseTo(now().minusHours(52), within(1, SECONDS))
    }

    @Test
    fun `parse now plus hours`() {
        val dateTime = parseDateTime("now+52h").trim()
        assertThat(dateTime).isCloseTo(now().plusHours(52), within(1, SECONDS))
    }

    @Test
    fun `parse now minus minutes`() {
        val dateTime = parseDateTime("now-60m").trim()
        assertThat(dateTime).isCloseTo(now().minusMinutes(60), within(1, SECONDS))
    }

    @Test
    fun `parse now plus minutes`() {
        val dateTime = parseDateTime("now+60m").trim()
        assertThat(dateTime).isCloseTo(now().plusMinutes(60), within(1, SECONDS))
    }

    @Test
    fun `parse now minus seconds`() {
        val dateTime = parseDateTime("now-4s").trim()
        assertThat(dateTime).isCloseTo(now().minusSeconds(4), within(1, SECONDS))
    }

    @Test
    fun `parse now plus seconds`() {
        val dateTime = parseDateTime("now+4s").trim()
        assertThat(dateTime).isCloseTo(now().plusSeconds(4), within(1, SECONDS))
    }

    private fun now(): LocalDateTime = LocalDateTime.now().truncatedTo(SECONDS)

    private fun LocalDateTime.trim(): LocalDateTime = this.truncatedTo(SECONDS)
}
