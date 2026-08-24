package dev.slne.surf.nametag.core.client.service

import dev.slne.surf.api.core.luckperms.LuckPermsAccess
import dev.slne.surf.api.core.luckperms.prefix
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.nametag.api.nametag.Nametag
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

    private val clanTagSpacer by lazy { NametagPlatform.shift(4) }

    private fun isPrevented(player: NametagPlayer) = player.uuid in preventNametagUpdateUuids

    fun preventUpdates(uuid: UUID) {
        preventNametagUpdateUuids.add(uuid)
    }

    fun allowUpdates(uuid: UUID) {
        preventNametagUpdateUuids.remove(uuid)
    }

    private suspend fun clanTag(player: NametagPlayer) = if (NametagPlatform.clanAvailable) {
        ClanHook.getClanTag(player.uuid)
    } else {
        Component.empty()
    }

    private fun createNameTag(player: NametagPlayer, clanTag: Component): Nametag {
        val playerUuid = player.uuid
        val prefixText = LuckPermsAccess.getUser(playerUuid)?.prefix

        return nametag {
            prefix {
                if (!prefixText.isNullOrEmpty()) {
                    append(miniMessage.deserialize(prefixText))
                }
            }

            playerName(player.name)

            suffix {
                spacer(clanTagSpacer)
                append(clanTag)

                if (NametagPlatform.contentCreatorAvailable) {
                    appendSpace()
                    append(ContentCreatorHook.renderLiveTag(playerUuid))
                }
            }
        }
    }

    suspend fun handleJoin(player: NametagPlayer) {
        if (isPrevented(player) || !player.isOnline) {
            return
        }

        val playerUuid = player.uuid
        val playerName = player.name

        val playerNametag = createNameTag(player, clanTag(player))
        val config = NametagConfig.getConfig()

        for (viewer in NametagPlatform.onlinePlayers()) {
            if (!player.isOnline) {
                return
            }

            if (isPrevented(viewer)) {
                continue
            }

            val viewerName = viewer.name
            viewer.showNametag(teamName(playerName, viewerName), playerNametag, config)

            if (viewer.uuid != playerUuid) {
                val viewerNametag = createNameTag(viewer, clanTag(viewer))

                if (viewer.isOnline) {
                    player.showNametag(teamName(viewerName, playerName), viewerNametag, config)
                }
            }
        }
    }

    suspend fun handleJoin(player: NametagPlayer, viewer: NametagPlayer) {
        if (isPrevented(player) || isPrevented(viewer)) {
            return
        }

        val nameTag = createNameTag(player, clanTag(player))

        if (!player.isOnline) {
            return
        }

        viewer.showNametag(
            teamName(player.name, viewer.name),
            nameTag,
            NametagConfig.getConfig()
        )
    }

    suspend fun handleDataUpdate(player: NametagPlayer) {
        if (isPrevented(player)) {
            return
        }

        val nameTag = createNameTag(player, clanTag(player))

        if (!player.isOnline) {
            return
        }

        val config = NametagConfig.getConfig()
        val playerName = player.name

        for (viewer in NametagPlatform.onlinePlayers()) {
            if (isPrevented(viewer)) {
                continue
            }

            viewer.updateNametag(teamName(playerName, viewer.name), nameTag, config)
        }
    }

    fun handleLeave(player: NametagPlayer) {
        if (!isPrevented(player)) {
            val playerName = player.name

            for (viewer in NametagPlatform.onlinePlayers()) {
                if (isPrevented(viewer)) {
                    continue
                }

                viewer.hideNametag(teamName(playerName, viewer.name))
            }
        }

        allowUpdates(player.uuid)
    }

    fun handleLeave(player: NametagPlayer, viewer: NametagPlayer) {
        if (isPrevented(player) || isPrevented(viewer)) {
            return
        }

        viewer.hideNametag(teamName(player.name, viewer.name))
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
