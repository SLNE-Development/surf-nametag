package dev.slne.surf.nametag.core.client.config

import dev.slne.surf.api.core.config.SpongeYmlConfigClass
import dev.slne.surf.nametag.core.client.platform.NametagPlatform
import net.kyori.adventure.text.format.NamedTextColor
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
data class NametagConfig(
    @param:Comment("Visibility of nametags. Options: ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM")
    val nameTagVisibility: NametagVisibility = NametagVisibility.ALWAYS,

    @param:Comment("Collision rule of nametags. Options: ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM")
    val collisionRule: NametagCollisionRule = NametagCollisionRule.ALWAYS,

    @param:Comment("Color of player names in nametags. Options: BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE")
    val nameColor: NamedTextColor = NamedTextColor.GRAY,

    @param:Comment("Option data of nametags. Options: NONE, FRIENDLY_FIRE, FRIENDLY_CAN_SEE_INVISIBLE, ALL")
    val optionData: NametagOptionData = NametagOptionData.NONE
) {
    companion object : SpongeYmlConfigClass<NametagConfig>(
        NametagConfig::class.java,
        NametagPlatform.dataPath,
        "config.yml"
    )
}
