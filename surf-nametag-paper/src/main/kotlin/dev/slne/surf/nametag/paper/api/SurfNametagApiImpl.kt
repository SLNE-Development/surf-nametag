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

    override fun setPrefix(
        player: UUID,
        viewer: UUID,
        prefix: Component
    ) = nametagService.setPrefix(player, viewer, prefix)

    override fun setSuffix(
        player: UUID,
        viewer: UUID,
        suffix: Component
    ) = nametagService.setSuffix(player, viewer, suffix)

    override fun resetPrefix(player: UUID, viewer: UUID) =
        nametagService.resetPrefix(player, viewer)

    override fun resetSuffix(player: UUID, viewer: UUID) =
        nametagService.resetSuffix(player, viewer)
}