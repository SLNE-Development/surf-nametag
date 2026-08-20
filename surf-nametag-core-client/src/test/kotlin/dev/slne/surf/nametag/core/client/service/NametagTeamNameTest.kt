package dev.slne.surf.nametag.core.client.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class NametagTeamNameTest {

    @Test
    fun `team name pairs the player with the viewer`() {
        assertEquals("red-blue", teamName("red", "blue"))
    }

    @Test
    fun `team name is scoped to a single viewer`() {
        assertNotEquals(teamName("red", "blue"), teamName("blue", "red"))
    }
}
