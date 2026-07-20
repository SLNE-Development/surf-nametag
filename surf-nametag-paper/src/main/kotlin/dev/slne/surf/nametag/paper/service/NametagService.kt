package dev.slne.surf.nametag.paper.service

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import dev.slne.surf.api.core.luckperms.getLuckPermsUser
import dev.slne.surf.api.core.luckperms.prefix
import dev.slne.surf.api.core.minimessage.miniMessage
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

    private fun createNameTag(player: Player, viewer: Player, clanTag: Component) = nametag {
        prefix {
            append(miniMessage.deserialize(player.getLuckPermsUser().prefix))
        }

        playerName(player.name)

        suffix {
            append(clanTag)

            if (plugin.checkContentCreator()) {
                appendSpace()
                append(ContentCreatorHook.renderLiveTag(player.uniqueId))
            }
        }
    }

    private fun createTeamPacket(
        relationString: String,
        mode: WrapperPlayServerTeams.TeamMode,
        nameTag: Nametag,
        config: NametagConfig
    ) =
        WrapperPlayServerTeams(
            relationString,
            mode,
            WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.text("surf-nametag-${relationString}"),
                nameTag.prefix,
                nameTag.suffix,
                config.nameTagVisibility,
                config.collisionRule,
                config.nameColor,
                config.optionData
            )
        )

    private val nullScoreboardInfo: WrapperPlayServerTeams.ScoreBoardTeamInfo? = null

    suspend fun handleJoin(player: Player) {
        val config = NametagConfig.getConfig()

        if (player.uniqueId in preventNametagUpdateUuids) {
            return
        }


        val playerClanTag = if (plugin.checkSurfClan()) {
            ClanHook.getClanTag(player.uniqueId)
        } else {
            Component.empty()
        }

        Bukkit.getOnlinePlayers().forEach {
            if (it.uniqueId in preventNametagUpdateUuids) {
                return@forEach
            }

            val nameTag = createNameTag(player, it, playerClanTag)

            it.sendPacket(
                createTeamPacket(
                    "${player.uniqueId}-${it.uniqueId}",
                    WrapperPlayServerTeams.TeamMode.CREATE,
                    nameTag,
                    config
                )
            )

            it.sendPacket(
                WrapperPlayServerTeams(
                    "${it.uniqueId}-${player.uniqueId}",
                    WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                    nullScoreboardInfo,
                    player.name
                )
            )

            if (it.uniqueId != player.uniqueId) {
                val viewerClanTag = if (plugin.checkSurfClan()) {
                    ClanHook.getClanTag(it.uniqueId)
                } else {
                    Component.empty()
                }

                val reverseNameTag = createNameTag(it, player, viewerClanTag)

                player.sendPacket(
                    createTeamPacket(
                        "${it.uniqueId}-${player.uniqueId}",
                        WrapperPlayServerTeams.TeamMode.CREATE,
                        reverseNameTag,
                        config
                    )
                )

                player.sendPacket(
                    WrapperPlayServerTeams(
                        "${player.uniqueId}-${it.uniqueId}",
                        WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                        nullScoreboardInfo,
                        it.name
                    )
                )
            }
        }
    }

    suspend fun handleDataUpdate(player: Player) {
        if (player.uniqueId in preventNametagUpdateUuids) {
            return
        }

        val clanTag = if (plugin.checkSurfClan()) {
            ClanHook.getClanTag(player.uniqueId)
        } else {
            Component.empty()
        }

        forEachPlayer {
            if (it.uniqueId in preventNametagUpdateUuids) {
                return@forEachPlayer
            }

            val nameTag = createNameTag(player, it, clanTag)
            val teamPacket = createTeamPacket(
                "${player.uniqueId}-${it.uniqueId}",
                WrapperPlayServerTeams.TeamMode.UPDATE,
                nameTag,
                NametagConfig.getConfig()
            )

            it.sendPacket(teamPacket)
        }
    }

    fun handleLeave(player: Player) {
        forEachPlayer {
            handleLeave(player, it)
        }
    }

    suspend fun handleJoin(player: Player, viewer: Player) {
        if (shouldPrevent(player, viewer)) {
            return
        }

        val config = NametagConfig.getConfig()

        val playerClanTag = if (plugin.checkSurfClan()) {
            ClanHook.getClanTag(player.uniqueId)
        } else {
            Component.empty()
        }

        val nameTag = createNameTag(player, viewer, playerClanTag)

        viewer.sendPacket(
            createTeamPacket(
                "${player.uniqueId}-${viewer.uniqueId}",
                WrapperPlayServerTeams.TeamMode.CREATE,
                nameTag,
                config
            )
        )

        viewer.sendPacket(
            WrapperPlayServerTeams(
                "${viewer.uniqueId}-${player.uniqueId}",
                WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                nullScoreboardInfo,
                player.name
            )
        )
    }

    fun handleLeave(player: Player, viewer: Player) {
        if (shouldPrevent(player, viewer)) {
            return
        }

        viewer.sendPacket(
            WrapperPlayServerTeams(
                "${player.uniqueId}-${viewer.uniqueId}",
                WrapperPlayServerTeams.TeamMode.REMOVE,
                nullScoreboardInfo,
                player.name
            )
        )

        viewer.sendPacket(
            WrapperPlayServerTeams(
                "${viewer.uniqueId}-${player.uniqueId}",
                WrapperPlayServerTeams.TeamMode.REMOVE,
                nullScoreboardInfo,
                viewer.name
            )
        )
    }
}