package dev.slne.surf.nametag.paper.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import org.bukkit.entity.Player

fun Player.sendPacket(vararg packets: PacketWrapper<*>) =
    packets.forEach { PacketEvents.getAPI().playerManager.sendPacket(this, it) }