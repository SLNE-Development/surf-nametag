package dev.slne.surf.nametag.paper.service

import dev.slne.surf.nametag.api.NametagLine
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.requiredService

class NametagService {
    val nametags = mutableObjectListOf<NametagLine>()

    companion object {
        val INSTANCE = requiredService<NametagService>()
    }
}

val nametagService get() = NametagService.INSTANCE