package dev.slne.surf.nametag.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.nametag.api.SurfNametagApi
import dev.slne.surf.nametag.paper.service.nametagService
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services
import java.util.UUID

@AutoService(SurfNametagApi::class)
class SurfNametagApiImpl : SurfNametagApi, Services.Fallback {
    override fun setNametag(player: UUID, nametag: Component) = nametagService.setNametag(player, nametag)
    override fun setNametag(player: UUID, nametag: String) = nametagService.setNametag(player, nametag)
    override fun resetNametag(player: UUID) = nametagService.clearNametag(player)
}