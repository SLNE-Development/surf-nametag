package dev.slne.surf.nametag.api

import java.util.*

data class NametagLine(
    val player: UUID,
    val viewer: UUID,
    val entityId: Int
)
