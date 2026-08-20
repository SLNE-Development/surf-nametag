package dev.slne.surf.nametag.paper.platform

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import dev.slne.surf.nametag.core.client.config.NametagCollisionRule
import dev.slne.surf.nametag.core.client.config.NametagOptionData
import dev.slne.surf.nametag.core.client.config.NametagVisibility

fun NametagVisibility.toPacketEvents(): WrapperPlayServerTeams.NameTagVisibility = when (this) {
    NametagVisibility.ALWAYS -> WrapperPlayServerTeams.NameTagVisibility.ALWAYS
    NametagVisibility.NEVER -> WrapperPlayServerTeams.NameTagVisibility.NEVER
    NametagVisibility.HIDE_FOR_OTHER_TEAMS -> WrapperPlayServerTeams.NameTagVisibility.HIDE_FOR_OTHER_TEAMS
    NametagVisibility.HIDE_FOR_OWN_TEAM -> WrapperPlayServerTeams.NameTagVisibility.HIDE_FOR_OWN_TEAM
}

fun NametagCollisionRule.toPacketEvents(): WrapperPlayServerTeams.CollisionRule = when (this) {
    NametagCollisionRule.ALWAYS -> WrapperPlayServerTeams.CollisionRule.ALWAYS
    NametagCollisionRule.NEVER -> WrapperPlayServerTeams.CollisionRule.NEVER
    NametagCollisionRule.PUSH_OTHER_TEAMS -> WrapperPlayServerTeams.CollisionRule.PUSH_OTHER_TEAMS
    NametagCollisionRule.PUSH_OWN_TEAM -> WrapperPlayServerTeams.CollisionRule.PUSH_OWN_TEAM
}

fun NametagOptionData.toPacketEvents(): WrapperPlayServerTeams.OptionData = when (this) {
    NametagOptionData.NONE -> WrapperPlayServerTeams.OptionData.NONE
    NametagOptionData.FRIENDLY_FIRE -> WrapperPlayServerTeams.OptionData.FRIENDLY_FIRE
    NametagOptionData.FRIENDLY_CAN_SEE_INVISIBLE -> WrapperPlayServerTeams.OptionData.FRIENDLY_CAN_SEE_INVISIBLE
    NametagOptionData.ALL -> WrapperPlayServerTeams.OptionData.ALL
}
