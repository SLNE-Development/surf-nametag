package dev.slne.surf.nametag.core.client.service

import dev.slne.surf.api.core.luckperms.LuckPermsAccess
import dev.slne.surf.api.core.luckperms.prefix
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.nametag.api.nametag.nametag
import dev.slne.surf.nametag.core.client.config.NametagConfig
import dev.slne.surf.nametag.core.client.hook.ClanHook
import dev.slne.surf.nametag.core.client.hook.ContentCreatorHook
import dev.slne.surf.nametag.core.client.platform.NametagPlatform
import dev.slne.surf.nametag.core.client.platform.NametagPlayer
import net.kyori.adventure.text.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object NametagService {
    private val preventNametagUpdateUuids = ConcurrentHashMap.newKeySet<UUID>()

    private fun shouldPrevent(vararg players: NametagPlayer): Boolean {
        return players.any { it.uuid in preventNametagUpdateUuids }
    }

    fun preventUpdates(uuid: UUID) {
        preventNametagUpdateUuids.add(uuid)
    }

    fun allowUpdates(uuid: UUID) {
        preventNametagUpdateUuids.remove(uuid)
    }

    private fun teamName(player: NametagPlayer, viewer: NametagPlayer) =
        teamName(player.name, viewer.name)

    private suspend fun clanTag(player: NametagPlayer) = if (NametagPlatform.clanAvailable) {
        ClanHook.getClanTag(player.uuid)
    } else {
        Component.empty()
    }

    private fun createNameTag(player: NametagPlayer, clanTag: Component) = nametag {
        prefix {
            append(miniMessage.deserialize(LuckPermsAccess.getUser(player.uuid)?.prefix ?: ""))
        }

        playerName(player.name)

        suffix {
            spacer(NametagPlatform.shift(4))
            append(clanTag)

            if (NametagPlatform.contentCreatorAvailable) {
                appendSpace()
                append(ContentCreatorHook.renderLiveTag(player.uuid))
            }
        }
    }

    suspend fun handleJoin(player: NametagPlayer) {
        NametagPlatform.onlinePlayers().forEach { viewer ->
            handleJoin(player, viewer)

            if (viewer.uuid != player.uuid) {
                handleJoin(viewer, player)
            }
        }
    }

    suspend fun handleJoin(player: NametagPlayer, viewer: NametagPlayer) {
        if (shouldPrevent(player, viewer)) {
            return
        }

        val nameTag = createNameTag(player, clanTag(player))

        viewer.showNametag(
            teamName(player, viewer),
            nameTag,
            NametagConfig.getConfig()
        )
    }

    suspend fun handleDataUpdate(player: NametagPlayer) {
        if (shouldPrevent(player)) {
            return
        }

        val nameTag = createNameTag(player, clanTag(player))
        val config = NametagConfig.getConfig()

        NametagPlatform.onlinePlayers().forEach { viewer ->
            if (shouldPrevent(viewer)) {
                return@forEach
            }

            viewer.updateNametag(teamName(player, viewer), nameTag, config)
        }
    }

    fun handleLeave(player: NametagPlayer) {
        NametagPlatform.onlinePlayers().forEach {
            handleLeave(player, it)
        }

        allowUpdates(player.uuid)
    }

    fun handleLeave(player: NametagPlayer, viewer: NametagPlayer) {
        if (shouldPrevent(player, viewer)) {
            return
        }

        viewer.hideNametag(teamName(player, viewer))
    }
}

/**
 * The name of the team [viewerName] sees the nametag of [playerName] under.
 *
 * Nametags are scoped to a single viewer, so the pair of names has to be part of the team name.
 */
fun teamName(playerName: String, viewerName: String) = "$playerName-$viewerName"

/**
 * The display name [teamName] is registered under.
 */
fun teamDisplayName(teamName: String): Component = Component.text("surf-nametag-${teamName}")
