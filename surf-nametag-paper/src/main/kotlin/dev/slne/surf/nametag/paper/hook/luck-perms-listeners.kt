package dev.slne.surf.nametag.paper.hook

import dev.slne.surf.nametag.core.client.hook.LuckPermsHook
import dev.slne.surf.nametag.paper.plugin
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User

fun registerLuckPermsListeners() {
    val eventBus = LuckPermsHook.luckPerms.eventBus

    eventBus.subscribe(plugin, NodeAddEvent::class.java) { event ->
        val user = event.target as? User ?: return@subscribe
        LuckPermsHook.updatePlayer(user)
    }

    eventBus.subscribe(plugin, NodeRemoveEvent::class.java) { event ->
        val user = event.target as? User ?: return@subscribe
        LuckPermsHook.updatePlayer(user)
    }
}
