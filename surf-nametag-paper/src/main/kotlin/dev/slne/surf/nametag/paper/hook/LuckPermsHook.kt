package dev.slne.surf.nametag.paper.hook

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.nametag.paper.plugin
import dev.slne.surf.nametag.paper.service.NametagService
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit

object LuckPermsHook {
    private val luckPerms by lazy {
        LuckPermsProvider.get()
    }

    fun load() {
        luckPerms.eventBus.subscribe(plugin, NodeAddEvent::class.java) { event ->
            val user = event.target as? User ?: return@subscribe
            val player = Bukkit.getPlayer(user.uniqueId) ?: return@subscribe
            
            plugin.launch {
                NametagService.handleDataUpdate(player)
            }
        }

        luckPerms.eventBus.subscribe(plugin, NodeRemoveEvent::class.java) { event ->
            val user = event.target as? User ?: return@subscribe
            val player = Bukkit.getPlayer(user.uniqueId) ?: return@subscribe

            plugin.launch {
                NametagService.handleDataUpdate(player)
            }
        }
    }
}