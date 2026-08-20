package dev.slne.surf.nametag.core.client.hook

import dev.slne.surf.nametag.core.client.platform.NametagPlatform
import dev.slne.surf.nametag.core.client.service.NametagService
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User

object LuckPermsHook {
    val luckPerms by lazy {
        LuckPermsProvider.get()
    }

    fun updatePlayer(user: User) {
        val player = NametagPlatform.player(user.uniqueId) ?: return

        NametagPlatform.launch {
            NametagService.handleDataUpdate(player)
        }
    }
}
