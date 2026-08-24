package dev.slne.surf.nametag.core.client.platform

import dev.slne.surf.nametag.api.nametag.Nametag
import dev.slne.surf.nametag.core.client.config.NametagConfig
import java.util.*

/**
 * A player as the shared nametag logic sees them.
 *
 * Nametags are scoped to a single viewer, so every method on this interface is called on the
 * player who is meant to see the team, not on the player the nametag belongs to.
 */
interface NametagPlayer {

    val uuid: UUID

    /**
     * The name the nametag team is built around.
     */
    val name: String

    /**
     * Whether this player is still connected to this server.
     */
    val isOnline: Boolean

    /**
     * Shows [nametag] under [teamName] to this player, rendered with [config].
     */
    fun showNametag(teamName: String, nametag: Nametag, config: NametagConfig)

    /**
     * Replaces what [teamName] shows to this player with [nametag], rendered with [config].
     */
    fun updateNametag(teamName: String, nametag: Nametag, config: NametagConfig)

    /**
     * Stops showing [teamName] to this player.
     */
    fun hideNametag(teamName: String)
}
