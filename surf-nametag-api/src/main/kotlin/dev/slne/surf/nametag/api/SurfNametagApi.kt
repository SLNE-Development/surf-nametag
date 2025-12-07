package dev.slne.surf.nametag.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import java.util.UUID

interface SurfNametagApi {
    fun setNametag(player: UUID, nametag: Component)
    fun setNametag(player: UUID, nametag: String)

    fun resetNametag(player: UUID)

    companion object {
        val INSTANCE = requiredService<SurfNametagApi>()
    }
}

val surfNametagApi get() = SurfNametagApi.INSTANCE