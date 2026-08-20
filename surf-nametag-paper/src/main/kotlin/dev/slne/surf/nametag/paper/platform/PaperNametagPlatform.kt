package dev.slne.surf.nametag.paper.platform

import com.github.shynixn.mccoroutine.folia.launch
import com.google.auto.service.AutoService
import dev.slne.surf.api.paper.inventory.framework.view.util.shift as glyphShift
import dev.slne.surf.nametag.core.client.platform.NametagPlatform
import dev.slne.surf.nametag.paper.plugin
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Bukkit
import java.util.*

@AutoService(NametagPlatform::class)
class PaperNametagPlatform : NametagPlatform {
    override val dataPath get() = plugin.dataPath

    override val clanAvailable get() = plugin.checkSurfClan()
    override val contentCreatorAvailable get() = plugin.checkContentCreator()

    override fun onlinePlayers() = Bukkit.getOnlinePlayers().map { PaperNametagPlayer(it) }

    override fun player(playerUuid: UUID) =
        Bukkit.getPlayer(playerUuid)?.let { PaperNametagPlayer(it) }

    override fun shift(amount: Int) = glyphShift(amount)

    override fun launch(block: suspend CoroutineScope.() -> Unit) = plugin.launch(block = block)
}
