package dev.slne.surf.nametag.minestom.platform

import dev.slne.surf.nametag.api.nametag.Nametag
import dev.slne.surf.nametag.core.client.config.NametagConfig
import dev.slne.surf.nametag.core.client.platform.NametagPlayer
import dev.slne.surf.nametag.core.client.service.teamDisplayName
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.TeamsPacket
import java.util.*

class MinestomNametagPlayer(private val player: Player) : NametagPlayer {
    override val uuid: UUID get() = player.uuid
    override val name: String get() = player.username
    override val isOnline: Boolean get() = player.isOnline

    override fun showNametag(teamName: String, nametag: Nametag, config: NametagConfig) {
        player.sendPacket(
            TeamsPacket(
                teamName,
                TeamsPacket.CreateTeamAction(
                    teamSettings(teamName, nametag, config),
                    listOf(nametag.playerName)
                )
            )
        )
    }

    override fun updateNametag(teamName: String, nametag: Nametag, config: NametagConfig) {
        player.sendPacket(
            TeamsPacket(
                teamName,
                TeamsPacket.UpdateTeamAction(teamSettings(teamName, nametag, config))
            )
        )
    }

    override fun hideNametag(teamName: String) {
        player.sendPacket(TeamsPacket(teamName, TeamsPacket.RemoveTeamAction()))
    }

    private fun teamSettings(
        teamName: String,
        nameTag: Nametag,
        config: NametagConfig
    ) = TeamsPacket.Settings(
        teamDisplayName(teamName),
        nameTag.prefix,
        nameTag.suffix,
        config.nameTagVisibility.toMinestom(),
        config.collisionRule.toMinestom(),
        config.nameColor.toTeamColor(),
        config.optionData.friendlyFlags
    )
}
