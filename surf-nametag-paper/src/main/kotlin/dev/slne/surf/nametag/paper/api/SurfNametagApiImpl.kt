package dev.slne.surf.nametag.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.nametag.api.SurfNametagApi
import net.kyori.adventure.util.Services

@AutoService(SurfNametagApi::class)
class SurfNametagApiImpl : SurfNametagApi, Services.Fallback {

}