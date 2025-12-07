package dev.slne.surf.nametag.paper.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import java.util.UUID

class NametagService {
    fun setNametag(playerUuid: UUID, nametag: String) {

    }

    fun setNametag(playerUuid: UUID, nametag: Component) {

    }

    fun clearNametag(playerUuid: UUID) {

    }

    companion object {
        val INSTANCE = requiredService<NametagService>()
    }
}

val nametagService get() = NametagService.INSTANCE