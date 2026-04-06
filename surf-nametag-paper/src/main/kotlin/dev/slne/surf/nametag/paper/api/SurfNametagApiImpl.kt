package dev.slne.surf.nametag.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.nametag.api.SurfNametagApi
import dev.slne.surf.nametag.paper.service.nametagService
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SurfNametagApi::class)
class SurfNametagApiImpl : SurfNametagApi, Services.Fallback {
    override fun showNametag(player: UUID, viewer: UUID) =
        nametagService.showNametag(player, viewer)

    override fun hideNametag(player: UUID, viewer: UUID) =
        nametagService.hideNametag(player, viewer)

    override fun setNametag(player: UUID, viewer: UUID, nametag: Component) =
        nametagService.setNametag(player, viewer, nametag)

    override fun resetNametag(player: UUID, viewer: UUID) =
        nametagService.resetNametag(player, viewer)
}