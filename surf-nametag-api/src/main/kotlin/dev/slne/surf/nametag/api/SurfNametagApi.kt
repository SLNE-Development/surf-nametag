package dev.slne.surf.nametag.api

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.nametag.api.nametag.NametagVisibility
import java.util.*

private val api = requiredService<SurfNametagApi>()

interface SurfNametagApi {
    fun preventNametagUpdate(playerUuid: UUID): Boolean

    fun allowNametagUpdate(playerUuid: UUID): Boolean

    /**
     * Shows the nametag of [playerUuid] with [visibility] to every viewer, instead of the configured
     * visibility.
     *
     * Viewers who already see the nametag are updated right away. The override lasts until
     * [resetNametagVisibility] or until the player leaves the server.
     */
    fun setNametagVisibility(playerUuid: UUID, visibility: NametagVisibility)

    /**
     * Returns the nametag of [playerUuid] to the configured visibility and updates every viewer.
     *
     * Does nothing when no override is set.
     */
    fun resetNametagVisibility(playerUuid: UUID)

    companion object : SurfNametagApi by api
}