package dev.slne.surf.nametag.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.nametag.api.SurfNametagApi
import dev.slne.surf.nametag.paper.service.NametagService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SurfNametagApi::class)
class SurfNametagApiImpl : SurfNametagApi, Services.Fallback {
    override fun preventNametagUpdate(playerUuid: UUID): Boolean {
        NametagService.preventUpdates(playerUuid)
        return true
    }
}