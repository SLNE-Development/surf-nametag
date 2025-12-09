package dev.slne.surf.nametag.paper.hook

import dev.slne.surf.nametag.paper.plugin
import dev.slne.surf.nametag.paper.service.nametagService
import dev.slne.surf.surfapi.bukkit.api.extensions.pluginManager
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit
import java.util.*

object LuckPermsHook {
    fun isEnabled() = pluginManager.isPluginEnabled("LuckPerms")

    private val luckPerms by lazy {
        LuckPermsProvider.get()
    }

    fun getPrefix(player: UUID) =
        if (isEnabled()) luckPerms.userManager.getUser(player)?.cachedData?.metaData?.prefix
            ?: "" else ""

    fun getSuffix(player: UUID) =
        if (isEnabled()) luckPerms.userManager.getUser(player)?.cachedData?.metaData?.suffix
            ?: "" else ""

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