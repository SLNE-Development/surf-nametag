package dev.slne.surf.nametag.paper.service

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import dev.slne.surf.nametag.paper.hook.LuckPermsHook
import dev.slne.surf.nametag.paper.util.sendPacket
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class NametagService {
    private val mm = MiniMessage.miniMessage()
    private fun teamName(player: UUID) = "surf_nametag_$player"

    private val prefixOverrides = mutableMapOf<Pair<UUID, UUID>, Component>()
    private val suffixOverrides = mutableMapOf<Pair<UUID, UUID>, Component>()

    fun handleJoin(joined: Player) {
        val joinedId = joined.uniqueId

        forEachPlayer {
            val otherId = it.uniqueId

            showNametag(joinedId, otherId)
            resetPrefix(joinedId, otherId)
            resetSuffix(joinedId, otherId)

            showNametag(otherId, joinedId)
            resetPrefix(otherId, joinedId)
            resetSuffix(otherId, joinedId)
        }
    }

    fun handleDataUpdate(player: Player) {
        val playerId = player.uniqueId

        forEachPlayer {
            val viewerId = it.uniqueId

            showNametag(playerId, viewerId)
            applyOverridesOrFallback(playerId, viewerId)
        }
    }

    fun showNametag(player: UUID, viewer: UUID) {
        updateTeam(player, viewer) { old ->
            cloneWith(old, visibility = WrapperPlayServerTeams.NameTagVisibility.ALWAYS)
        }
    }

    fun hideNametag(player: UUID, viewer: UUID) {
        updateTeam(player, viewer) { old ->
            cloneWith(old, visibility = WrapperPlayServerTeams.NameTagVisibility.NEVER)
        }
    }

    fun setPrefix(player: UUID, viewer: UUID, prefix: Component) {
        prefixOverrides[player to viewer] = prefix
        updateTeam(player, viewer) { old ->
            cloneWith(old, prefix = prefix)
        }
    }

    fun setSuffix(player: UUID, viewer: UUID, suffix: Component) {
        suffixOverrides[player to viewer] = suffix
        updateTeam(player, viewer) { old ->
            cloneWith(old, suffix = suffix)
        }
    }

    fun resetPrefix(player: UUID, viewer: UUID) {
        prefixOverrides.remove(player to viewer)
        val lpPrefix = mm.deserialize(LuckPermsHook.getPrefix(player))

        updateTeam(player, viewer) { old ->
            cloneWith(old, prefix = lpPrefix)
        }
    }

    fun resetSuffix(player: UUID, viewer: UUID) {
        suffixOverrides.remove(player to viewer)
        val lpSuffix = mm.deserialize(LuckPermsHook.getSuffix(player))

        updateTeam(player, viewer) { old ->
            cloneWith(old, suffix = lpSuffix)
        }
    }

    private fun applyOverridesOrFallback(player: UUID, viewer: UUID) {
        val prefix = prefixOverrides[player to viewer]
            ?: mm.deserialize(LuckPermsHook.getPrefix(player))

        val suffix = suffixOverrides[player to viewer]
            ?: mm.deserialize(LuckPermsHook.getSuffix(player))

        updateTeam(player, viewer) { old ->
            cloneWith(old, prefix = prefix, suffix = suffix)
        }
    }

    private fun updateTeam(
        player: UUID,
        viewer: UUID,
        modifier: (WrapperPlayServerTeams.ScoreBoardTeamInfo) -> WrapperPlayServerTeams.ScoreBoardTeamInfo
    ) {
        val vwr = Bukkit.getPlayer(viewer) ?: return
        val plr = Bukkit.getPlayer(player) ?: return
        val team = teamName(player)

        val baseInfo = WrapperPlayServerTeams.ScoreBoardTeamInfo(
            Component.text(team),
            mm.deserialize(LuckPermsHook.getPrefix(player)),
            mm.deserialize(LuckPermsHook.getSuffix(player)),
            WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
            WrapperPlayServerTeams.CollisionRule.ALWAYS,
            null,
            WrapperPlayServerTeams.OptionData.NONE
        )

        val newInfo = modifier(baseInfo)

        val packetCreate = WrapperPlayServerTeams(
            team,
            WrapperPlayServerTeams.TeamMode.CREATE,
            newInfo,
            plr.name
        )

        val packetUpdate = WrapperPlayServerTeams(
            team,
            WrapperPlayServerTeams.TeamMode.UPDATE,
            newInfo
        )

        vwr.sendPacket(packetCreate, packetUpdate)
    }

    private fun cloneWith(
        original: WrapperPlayServerTeams.ScoreBoardTeamInfo,
        prefix: Component? = null,
        suffix: Component? = null,
        visibility: WrapperPlayServerTeams.NameTagVisibility? = null
    ): WrapperPlayServerTeams.ScoreBoardTeamInfo {
        return WrapperPlayServerTeams.ScoreBoardTeamInfo(
            original.displayName,
            prefix ?: original.prefix,
            suffix ?: original.suffix,
            visibility ?: original.tagVisibility,
            original.collisionRule,
            original.color,
            original.optionData
        )
    }

    companion object {
        val INSTANCE = NametagService()
    }
}

val nametagService get() = NametagService.INSTANCE