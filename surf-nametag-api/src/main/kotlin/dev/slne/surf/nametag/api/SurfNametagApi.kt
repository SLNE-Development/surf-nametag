package dev.slne.surf.nametag.api

import dev.slne.surf.api.core.util.requiredService
import java.util.*

private val api = requiredService<SurfNametagApi>()

interface SurfNametagApi {
    fun preventNametagUpdate(playerUuid: UUID): Boolean

    companion object : SurfNametagApi by api
}