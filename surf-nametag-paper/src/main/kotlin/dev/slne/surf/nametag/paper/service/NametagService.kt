package dev.slne.surf.nametag.paper.service

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Vector3f
import com.github.retrooper.packetevents.wrapper.play.server.*
import dev.slne.surf.nametag.paper.hook.LuckPermsHook
import dev.slne.surf.nametag.paper.plugin
import dev.slne.surf.nametag.paper.util.sendPacket
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.minimessage.miniMessage
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.random
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class NametagService {
    private val mm = MiniMessage.miniMessage()

    private val entityIds = mutableObject2ObjectMapOf<UUID, Int>()
    private val nametagOverrides = mutableObject2ObjectMapOf<Pair<UUID, UUID>, Component>()
    private val hiddenNametags = mutableObjectSetOf<Pair<UUID, UUID>>()
    private val spawnedDisplays = mutableObjectSetOf<Pair<UUID, UUID>>()
    private val teamCreatedFor = mutableObjectSetOf<UUID>()

    private fun getOrCreateEntityId(player: UUID): Int =
        entityIds.getOrPut(player) { random.nextInt() }

    fun handleJoin(joined: Player) {
        forEachPlayer { player ->
            sendTeamRefresh(player)
        }
    }

    fun handleQuit(player: Player) {
        val playerId = player.uniqueId
        val entityId = entityIds.remove(playerId)

        forEachPlayer { viewer ->
            if (viewer.uniqueId == playerId) return@forEachPlayer

            if (entityId != null) {
                val key = playerId to viewer.uniqueId
                if (key in spawnedDisplays) {
                    viewer.sendPacket(WrapperPlayServerDestroyEntities(entityId))
                    spawnedDisplays.remove(key)
                }
            }
        }

        cleanupPlayerState(playerId)
    }

    fun getVirtualPassengerId(vehicleEntityId: Int, viewerUuid: UUID): Int? {
        val player = Bukkit.getOnlinePlayers().find { it.entityId == vehicleEntityId }
            ?: return null
        val key = player.uniqueId to viewerUuid
        if (key !in spawnedDisplays) return null
        return entityIds[player.uniqueId]
    }

    fun ensureTeamMembership(viewer: Player, memberName: String) {
        if (viewer.uniqueId !in teamCreatedFor) {
            sendTeamRefresh(viewer)
        }
        sendTeamAddMember(viewer, memberName)
    }

    fun handlePlayerTracked(player: Player, viewer: Player) {
        val playerId = player.uniqueId
        val viewerId = viewer.uniqueId
        if (playerId == viewerId) return

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (!player.isOnline || !viewer.isOnline) return@Runnable
            val key = playerId to viewerId
            if (key !in hiddenNametags) {
                spawnTextDisplay(player, viewer)
            }
        }, 2L)
    }

    fun handlePlayerUntracked(playerEntityId: Int, viewer: Player) {
        val player = Bukkit.getOnlinePlayers().find { it.entityId == playerEntityId } ?: return
        val key = player.uniqueId to viewer.uniqueId
        if (key in spawnedDisplays) {
            spawnedDisplays.remove(key)
            val textDisplayEntityId = entityIds[player.uniqueId] ?: return
            viewer.sendPacket(WrapperPlayServerDestroyEntities(textDisplayEntityId))
        }
    }

    fun handleDataUpdate(player: Player) {
        val playerId = player.uniqueId

        forEachPlayer { viewer ->
            if (viewer.uniqueId == playerId) return@forEachPlayer
            val key = playerId to viewer.uniqueId
            if (key in spawnedDisplays && key !in hiddenNametags) {
                updateTextDisplayMetadata(playerId, viewer)
            }
        }
    }

    fun showNametag(player: UUID, viewer: UUID) {
        val key = player to viewer
        if (key !in hiddenNametags) return
        hiddenNametags.remove(key)

        val plr = Bukkit.getPlayer(player) ?: return
        val vwr = Bukkit.getPlayer(viewer) ?: return

        spawnTextDisplay(plr, vwr)
    }

    fun hideNametag(player: UUID, viewer: UUID) {
        val key = player to viewer
        hiddenNametags.add(key)

        val vwr = Bukkit.getPlayer(viewer) ?: return
        val entityId = entityIds[player] ?: return

        if (key in spawnedDisplays) {
            vwr.sendPacket(WrapperPlayServerDestroyEntities(entityId))
            spawnedDisplays.remove(key)
        }
    }

    fun setNametag(player: UUID, viewer: UUID, nametag: Component) {
        nametagOverrides[player to viewer] = nametag

        val vwr = Bukkit.getPlayer(viewer) ?: return
        val key = player to viewer
        if (key in spawnedDisplays && key !in hiddenNametags) {
            updateTextDisplayMetadata(player, vwr)
        }
    }

    fun resetNametag(player: UUID, viewer: UUID) {
        val key = player to viewer
        nametagOverrides.remove(key)

        val vwr = Bukkit.getPlayer(viewer) ?: return
        if (key in spawnedDisplays && key !in hiddenNametags) {
            updateTextDisplayMetadata(player, vwr)
        }
    }

    private fun buildNametagText(playerUuid: UUID, viewer: UUID): Component {
        nametagOverrides[playerUuid to viewer]?.let { return it }

        val player = Bukkit.getPlayer(playerUuid) ?: return Component.empty()
        val prefix = LuckPermsHook.getPrefix(playerUuid)

        return buildText {
            append(miniMessage.deserialize("$prefix${player.name}"))
        }
    }

    private fun spawnTextDisplay(player: Player, viewer: Player) {
        val playerId = player.uniqueId
        val viewerId = viewer.uniqueId
        val key = playerId to viewerId

        if (key in hiddenNametags) return

        if (key in spawnedDisplays) {
            updateTextDisplayMetadata(playerId, viewer)
            return
        }

        val entityId = getOrCreateEntityId(playerId)
        val location = player.location

        val spawnPacket = WrapperPlayServerSpawnEntity(
            entityId,
            UUID.randomUUID(),
            EntityTypes.TEXT_DISPLAY,
            SpigotConversionUtil.fromBukkitLocation(location),
            0f,
            0,
            null
        )

        val metadataPacket = createMetadataPacket(playerId, viewerId, entityId)

        val passengersPacket = WrapperPlayServerSetPassengers(
            player.entityId,
            intArrayOf(entityId)
        )

        viewer.sendPacket(spawnPacket, metadataPacket, passengersPacket)
        spawnedDisplays.add(key)
    }

    private fun updateTextDisplayMetadata(player: UUID, viewer: Player) {
        val entityId = entityIds[player] ?: return
        val metadataPacket = createMetadataPacket(player, viewer.uniqueId, entityId)
        viewer.sendPacket(metadataPacket)
    }

    private fun createMetadataPacket(
        player: UUID,
        viewer: UUID,
        entityId: Int
    ): WrapperPlayServerEntityMetadata {
        val nametagText = buildNametagText(player, viewer)

        val metadata = listOf(
            EntityData(BILLBOARD_INDEX, EntityDataTypes.BYTE, VERT_BILLBOARD),
            EntityData(
                TRANSLATION_INDEX, EntityDataTypes.VECTOR3F,
                Vector3f(0f, NAMETAG_Y_OFFSET, 0f)
            ),
            EntityData(
                SCALE_INDEX, EntityDataTypes.VECTOR3F,
                Vector3f(1f, 1f, 1f)
            ),
            EntityData(TEXT_INDEX, EntityDataTypes.ADV_COMPONENT, nametagText),
            EntityData(BACKGROUND_COLOR_INDEX, EntityDataTypes.INT, TRANSPARENT_BACKGROUND),
            EntityData(VIEW_RANGE_INDEX, EntityDataTypes.FLOAT, 1.0f),
            EntityData(SHADOW_RADIUS_INDEX, EntityDataTypes.FLOAT, 0f),
            EntityData(SHADOW_STRENGTH_INDEX, EntityDataTypes.FLOAT, 0f),
        )

        return WrapperPlayServerEntityMetadata(entityId, metadata)
    }

    private fun sendTeamRefresh(viewer: Player) {
        if (viewer.uniqueId in teamCreatedFor) {
            viewer.sendPacket(
                WrapperPlayServerTeams(
                    TEAM_NAME,
                    WrapperPlayServerTeams.TeamMode.REMOVE,
                    null as WrapperPlayServerTeams.ScoreBoardTeamInfo?
                )
            )
        }

        val members = mutableListOf<String>()
        forEachPlayer { members.add(it.name) }

        val teamInfo = WrapperPlayServerTeams.ScoreBoardTeamInfo(
            Component.empty(),
            Component.empty(),
            Component.empty(),
            WrapperPlayServerTeams.NameTagVisibility.NEVER,
            WrapperPlayServerTeams.CollisionRule.ALWAYS,
            null,
            WrapperPlayServerTeams.OptionData.NONE
        )

        viewer.sendPacket(
            WrapperPlayServerTeams(
                TEAM_NAME,
                WrapperPlayServerTeams.TeamMode.CREATE,
                teamInfo,
                members
            )
        )

        teamCreatedFor.add(viewer.uniqueId)
    }

    private fun sendTeamAddMember(viewer: Player, memberName: String) {
        if (viewer.uniqueId !in teamCreatedFor) return

        viewer.sendPacket(
            WrapperPlayServerTeams(
                TEAM_NAME,
                WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                null as WrapperPlayServerTeams.ScoreBoardTeamInfo?,
                memberName
            )
        )
    }

    private fun cleanupPlayerState(playerId: UUID) {
        prefixOverrides.keys.removeIf { it.first == playerId || it.second == playerId }
        suffixOverrides.keys.removeIf { it.first == playerId || it.second == playerId }
        nametagOverrides.keys.removeIf { it.first == playerId || it.second == playerId }
        hiddenNametags.removeIf { it.first == playerId || it.second == playerId }
        spawnedDisplays.removeIf { it.first == playerId || it.second == playerId }
        teamCreatedFor.remove(playerId)
    }

    companion object {
        private const val TEAM_NAME = "surf_no_nametag"

        private const val TRANSLATION_INDEX = 11
        private const val SCALE_INDEX = 12
        private const val BILLBOARD_INDEX = 15
        private const val VIEW_RANGE_INDEX = 17
        private const val SHADOW_RADIUS_INDEX = 18
        private const val SHADOW_STRENGTH_INDEX = 19

        private const val TEXT_INDEX = 23
        private const val BACKGROUND_COLOR_INDEX = 25

        private const val VERT_BILLBOARD: Byte = 1
        private const val NAMETAG_Y_OFFSET = 0.3f
        private const val TRANSPARENT_BACKGROUND = 0
    }
}

val nametagService = NametagService()