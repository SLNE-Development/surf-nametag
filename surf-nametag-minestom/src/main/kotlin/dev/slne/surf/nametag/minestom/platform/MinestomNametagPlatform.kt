package dev.slne.surf.nametag.minestom.platform

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.coroutine.minestomScope
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.surf.api.minestom.inventory.framework.view.util.shift as glyphShift
import dev.slne.surf.nametag.core.client.platform.NametagPlatform
import dev.slne.surf.nametag.minestom.NametagMinestomEntrypoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@AutoService(NametagPlatform::class)
class MinestomNametagPlatform : NametagPlatform {
    override val dataPath get() = NametagMinestomEntrypoint.dataPath

    override val clanAvailable = true
    override val contentCreatorAvailable = true

    override fun onlinePlayers() =
        ConnectionManager.onlinePlayers.map { MinestomNametagPlayer(it) }

    override fun player(playerUuid: UUID) =
        ConnectionManager.getOnlinePlayerByUuid(playerUuid)?.let { MinestomNametagPlayer(it) }

    override fun shift(amount: Int) = glyphShift(amount)

    override fun launch(block: suspend CoroutineScope.() -> Unit) =
        minestomScope.launch(block = block)
}
