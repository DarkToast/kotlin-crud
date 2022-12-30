package de.tarent.crud.controller

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime.now
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit.SECONDS

internal class ParseDateTimeSpec {
    @Test
    fun `parses iso`() {
        val dateTime: OffsetDateTime = parseDateTime("2022-12-29T13:55:56")
        assertThat(dateTime).isEqualTo(OffsetDateTime.parse("2022-12-29T13:55:56+01:00").trim())
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
        assertThat(dateTime).isEqualTo(now().trim())
    }

    @Test
    fun `parse now minus days`() {
        val dateTime = parseDateTime("now-4d").trim()
        assertThat(dateTime).isEqualTo(now().minusDays(4).trim())
    }

    @Test
    fun `parse now plus days`() {
        val dateTime = parseDateTime("now+4d").trim()
        assertThat(dateTime).isEqualTo(now().plusDays(4).trim())
    }

    @Test
    fun `parse now minus hours`() {
        val dateTime = parseDateTime("now-52h").trim()
        assertThat(dateTime).isEqualTo(now().minusHours(52).trim())
    }

    @Test
    fun `parse now plus hours`() {
        val dateTime = parseDateTime("now+52h").trim()
        assertThat(dateTime).isEqualTo(now().plusHours(52).trim())
    }

    @Test
    fun `parse now minus minutes`() {
        val dateTime = parseDateTime("now-60m").trim()
        assertThat(dateTime).isEqualTo(now().minusMinutes(60).trim())
    }

    @Test
    fun `parse now plus minutes`() {
        val dateTime = parseDateTime("now+60m").trim()
        assertThat(dateTime).isEqualTo(now().plusMinutes(60).trim())
    }

    @Test
    fun `parse now minus seconds`() {
        val dateTime = parseDateTime("now-4s").trim()
        assertThat(dateTime).isEqualTo(now().minusSeconds(4).trim())
    }

    @Test
    fun `parse now plus seconds`() {
        val dateTime = parseDateTime("now+4s").trim()
        assertThat(dateTime).isEqualTo(now().plusSeconds(4).trim())
    }

    private fun OffsetDateTime.trim(): OffsetDateTime = this.truncatedTo(SECONDS)
}