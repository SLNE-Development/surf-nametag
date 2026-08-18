package dev.slne.surf.nametag.paper.service

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import dev.slne.surf.api.core.luckperms.getLuckPermsUserOrNull
import dev.slne.surf.api.core.luckperms.prefix
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.api.paper.inventory.framework.view.util.shift
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.nametag.api.nametag.Nametag
import dev.slne.surf.nametag.api.nametag.nametag
import dev.slne.surf.nametag.paper.config.NametagConfig
import dev.slne.surf.nametag.paper.hook.ClanHook
import dev.slne.surf.nametag.paper.hook.ContentCreatorHook
import dev.slne.surf.nametag.paper.plugin
import dev.slne.surf.nametag.paper.util.sendPacket
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object NametagService {
    private val preventNametagUpdateUuids = ConcurrentHashMap.newKeySet<UUID>()

    private fun shouldPrevent(vararg players: Player): Boolean {
        return players.any { it.uniqueId in preventNametagUpdateUuids }
    }

    fun preventUpdates(uuid: UUID) {
        preventNametagUpdateUuids.add(uuid)
    }

    fun allowUpdates(uuid: UUID) {
        preventNametagUpdateUuids.remove(uuid)
    }

    private fun teamName(player: Player, viewer: Player) = "${player.name}-${viewer.name}"

    private suspend fun clanTag(player: Player) = if (plugin.checkSurfClan()) {
        ClanHook.getClanTag(player.uniqueId)
    } else {
        Component.empty()
    }

    private fun createNameTag(player: Player, clanTag: Component) = nametag {
        prefix {
            append(miniMessage.deserialize(player.getLuckPermsUserOrNull()?.prefix ?: ""))
        }

        playerName(player.name)

        suffix {
            spacer(shift(4))
            append(clanTag)

            if (plugin.checkContentCreator()) {
                appendSpace()
                append(ContentCreatorHook.renderLiveTag(player.uniqueId))
            }
        }
    }

    private fun createTeamPacket(
        teamName: String,
        mode: WrapperPlayServerTeams.TeamMode,
        nameTag: Nametag,
        config: NametagConfig
    ) =
        WrapperPlayServerTeams(
            teamName,
            mode,
            WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.text("surf-nametag-${teamName}"),
                nameTag.prefix,
                nameTag.suffix,
                config.nameTagVisibility,
                config.collisionRule,
                config.nameColor,
                config.optionData
            ),
            nameTag.playerName
        )

    private val nullScoreboardInfo: WrapperPlayServerTeams.ScoreBoardTeamInfo? = null

    suspend fun handleJoin(player: Player) {
        Bukkit.getOnlinePlayers().forEach { viewer ->
            handleJoin(player, viewer)

            if (viewer.uniqueId != player.uniqueId) {
                handleJoin(viewer, player)
            }
        }
    }

    suspend fun handleJoin(player: Player, viewer: Player) {
        if (shouldPrevent(player, viewer)) {
            return
        }

        val nameTag = createNameTag(player, clanTag(player))

        viewer.sendPacket(
            createTeamPacket(
                teamName(player, viewer),
                WrapperPlayServerTeams.TeamMode.CREATE,
                nameTag,
                NametagConfig.getConfig()
            )
        )
    }

    suspend fun handleDataUpdate(player: Player) {
        if (shouldPrevent(player)) {
            return
        }

        val nameTag = createNameTag(player, clanTag(player))
        val config = NametagConfig.getConfig()

        forEachPlayer { viewer ->
            if (shouldPrevent(viewer)) {
                return@forEachPlayer
            }

            viewer.sendPacket(
                createTeamPacket(
                    teamName(player, viewer),
                    WrapperPlayServerTeams.TeamMode.UPDATE,
                    nameTag,
                    config
                )
            )
        }
    }

    fun handleLeave(player: Player) {
        forEachPlayer {
            handleLeave(player, it)
        }

        allowUpdates(player.uniqueId)
    }

    fun handleLeave(player: Player, viewer: Player) {
        if (shouldPrevent(player, viewer)) {
            return
        }

        viewer.sendPacket(
            WrapperPlayServerTeams(
                teamName(player, viewer),
                WrapperPlayServerTeams.TeamMode.REMOVE,
                nullScoreboardInfo
            )
        )
    }
}