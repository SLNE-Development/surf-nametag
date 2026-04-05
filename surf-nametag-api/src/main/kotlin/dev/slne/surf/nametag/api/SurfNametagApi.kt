package dev.slne.surf.nametag.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import java.util.*

interface SurfNametagApi {
    fun showNametag(player: UUID, viewer: UUID)
    fun hideNametag(player: UUID, viewer: UUID)

    fun setNametag(player: UUID, viewer: UUID, nametag: Component)
    fun resetNametag(player: UUID, viewer: UUID)

    fun setPrefix(player: UUID, viewer: UUID, prefix: Component)
    fun setSuffix(player: UUID, viewer: UUID, suffix: Component)

    fun resetPrefix(player: UUID, viewer: UUID)
    fun resetSuffix(player: UUID, viewer: UUID)

    companion object {
        val INSTANCE = requiredService<SurfNametagApi>()
    }
}

val surfNametagApi get() = SurfNametagApi.INSTANCE