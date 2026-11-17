package dev.slne.surf.nametag.core.client.service

import dev.slne.surf.api.core.luckperms.LuckPermsAccess
import dev.slne.surf.api.core.luckperms.prefix
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.nametag.api.nametag.Nametag
import dev.slne.surf.nametag.api.nametag.NametagVisibility
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
    private val visibilityOverrides = ConcurrentHashMap<UUID, NametagVisibility>()

    private val clanTagSpacer by lazy { NametagPlatform.shift(4) }

    private fun isPrevented(player: NametagPlayer) = player.uuid in preventNametagUpdateUuids

    fun preventUpdates(uuid: UUID) {
        preventNametagUpdateUuids.add(uuid)
    }

    fun allowUpdates(uuid: UUID) {
        preventNametagUpdateUuids.remove(uuid)
    }

    /**
     * Shows the nametag of [uuid] with [visibility] to every viewer until [resetVisibility] or the
     * player leaves; viewers who already see it are updated asynchronously.
     */
    fun overrideVisibility(uuid: UUID, visibility: NametagVisibility) {
        if (visibilityOverrides.put(uuid, visibility) != visibility) refresh(uuid)
    }

    /**
     * Removes the visibility override of [uuid], if any, and updates every viewer asynchronously.
     */
    fun resetVisibility(uuid: UUID) {
        if (visibilityOverrides.remove(uuid) != null) refresh(uuid)
    }

    /**
     * The config the nametag of [uuid] is rendered with: [config] with the visibility override applied.
     */
    fun configFor(uuid: UUID, config: NametagConfig): NametagConfig {
        return visibilityOverrides[uuid]?.let { config.copy(nameTagVisibility = it) } ?: config
    }

    private fun refresh(uuid: UUID) {
        val player = NametagPlatform.player(uuid) ?: return
        NametagPlatform.launch { handleDataUpdate(player) }
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
            viewer.showNametag(teamName(playerName, viewerName), playerNametag, configFor(playerUuid, config))

            if (viewer.uuid != playerUuid) {
                val viewerNametag = createNameTag(viewer, clanTag(viewer))

                if (viewer.isOnline) {
                    player.showNametag(teamName(viewerName, playerName), viewerNametag, configFor(viewer.uuid, config))
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
            configFor(player.uuid, NametagConfig.getConfig())
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

        val config = configFor(player.uuid, NametagConfig.getConfig())
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
        visibilityOverrides.remove(player.uuid)
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
