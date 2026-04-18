package dev.slne.surf.nametag.paper

import com.github.retrooper.packetevents.PacketEvents
import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.extensions.pluginManager
import dev.slne.surf.nametag.paper.hook.ClanHook
import dev.slne.surf.nametag.paper.hook.ContentCreatorHook
import dev.slne.surf.nametag.paper.hook.LuckPermsHook
import dev.slne.surf.nametag.paper.listener.NametagListener
import dev.slne.surf.nametag.paper.listener.NametagPacketListener
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        NametagListener.register()
        PacketEvents.getAPI().eventManager.registerListener(NametagPacketListener)

        LuckPermsHook.load()

        if (checkSurfClan()) {
            ClanHook.createListeners()
        }

        if (checkContentCreator()) {
            ContentCreatorHook.registerListener()
        }
    }

    fun checkSurfClan() = pluginManager.isPluginEnabled("surf-clan-paper")
    fun checkContentCreator() = pluginManager.isPluginEnabled("surf-content-creator-paper")
}