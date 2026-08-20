package dev.slne.surf.nametag.core.client.config

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NametagOptionDataTest {

    @Test
    fun `friendly flags match the protocol bit mask`() {
        assertEquals(0, NametagOptionData.NONE.friendlyFlags)
        assertEquals(1, NametagOptionData.FRIENDLY_FIRE.friendlyFlags)
        assertEquals(2, NametagOptionData.FRIENDLY_CAN_SEE_INVISIBLE.friendlyFlags)
        assertEquals(3, NametagOptionData.ALL.friendlyFlags)
    }

    @Test
    fun `friendly flags combine the individual options`() {
        val combined = NametagOptionData.FRIENDLY_FIRE.friendlyFlags.toInt() or
                NametagOptionData.FRIENDLY_CAN_SEE_INVISIBLE.friendlyFlags.toInt()

        assertEquals(NametagOptionData.ALL.friendlyFlags.toInt(), combined)
    }
}
