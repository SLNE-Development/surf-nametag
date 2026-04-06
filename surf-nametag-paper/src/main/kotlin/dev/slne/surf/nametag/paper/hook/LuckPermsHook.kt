package dev.slne.surf.nametag.paper.hook

import dev.slne.surf.nametag.paper.plugin
import dev.slne.surf.nametag.paper.service.nametagService
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit
import java.util.*

object LuckPermsHook {
    private val luckPerms by lazy {
        LuckPermsProvider.get()
    }

    fun getPrefix(player: UUID) =
        luckPerms.userManager.getUser(player)?.cachedData?.metaData?.prefix
            ?: ""

    fun getSuffix(player: UUID) =
        luckPerms.userManager.getUser(player)?.cachedData?.metaData?.suffix
            ?: ""

    fun load() {
        luckPerms.eventBus.subscribe(plugin, NodeAddEvent::class.java) { event ->
            val user = event.target as? User ?: return@subscribe
            val player = Bukkit.getPlayer(user.uniqueId) ?: return@subscribe
            nametagService.handleDataUpdate(player)
        }

        luckPerms.eventBus.subscribe(plugin, NodeRemoveEvent::class.java) { event ->
            val user = event.target as? User ?: return@subscribe
            val player = Bukkit.getPlayer(user.uniqueId) ?: return@subscribe

            nametagService.handleDataUpdate(player)
        }
    }
}