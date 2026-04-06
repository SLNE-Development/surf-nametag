package dev.slne.surf.nametag.api

import dev.slne.surf.api.core.util.requiredService
import net.kyori.adventure.text.Component
import java.util.*

private val api = requiredService<SurfNametagApi>()

interface SurfNametagApi {
    fun showNametag(player: UUID, viewer: UUID)
    fun hideNametag(player: UUID, viewer: UUID)

    fun setNametag(player: UUID, viewer: UUID, nametag: Component)
    fun resetNametag(player: UUID, viewer: UUID)

    companion object : SurfNametagApi by api
}