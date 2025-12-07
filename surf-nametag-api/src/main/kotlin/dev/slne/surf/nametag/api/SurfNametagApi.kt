package dev.slne.surf.nametag.api

import dev.slne.surf.surfapi.core.api.util.requiredService

interface SurfNametagApi {

    companion object {
        val INSTANCE = requiredService<SurfNametagApi>()
    }
}

val surfNametagApi get() = SurfNametagApi.INSTANCE