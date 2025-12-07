package dev.slne.surf.nametag.api

import net.kyori.adventure.text.Component
import java.util.*

data class NametagLine(
    val player: UUID,
    val viewer: UUID,

    val index: Int,
    val component: Component,
    val entityId: Int
)
