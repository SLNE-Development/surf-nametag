package dev.slne.surf.nametag.minestom.platform

import dev.slne.surf.nametag.core.client.config.NametagCollisionRule
import dev.slne.surf.nametag.core.client.config.NametagVisibility
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.color.TeamColor
import net.minestom.server.network.packet.server.play.TeamsPacket

fun NametagVisibility.toMinestom(): TeamsPacket.NameTagVisibility = when (this) {
    NametagVisibility.ALWAYS -> TeamsPacket.NameTagVisibility.ALWAYS
    NametagVisibility.NEVER -> TeamsPacket.NameTagVisibility.NEVER
    NametagVisibility.HIDE_FOR_OTHER_TEAMS -> TeamsPacket.NameTagVisibility.HIDE_FOR_OTHER_TEAMS
    NametagVisibility.HIDE_FOR_OWN_TEAM -> TeamsPacket.NameTagVisibility.HIDE_FOR_OWN_TEAM
}

fun NametagCollisionRule.toMinestom(): TeamsPacket.CollisionRule = when (this) {
    NametagCollisionRule.ALWAYS -> TeamsPacket.CollisionRule.ALWAYS
    NametagCollisionRule.NEVER -> TeamsPacket.CollisionRule.NEVER
    NametagCollisionRule.PUSH_OTHER_TEAMS -> TeamsPacket.CollisionRule.PUSH_OTHER_TEAMS
    NametagCollisionRule.PUSH_OWN_TEAM -> TeamsPacket.CollisionRule.PUSH_OWN_TEAM
}

fun NamedTextColor.toTeamColor(): TeamColor? = TeamColor.fromName(name())
