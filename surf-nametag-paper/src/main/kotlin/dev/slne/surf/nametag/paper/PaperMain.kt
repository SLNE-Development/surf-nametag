package dev.slne.surf.nametag.paper

import com.github.retrooper.packetevents.PacketEvents
import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.nametag.paper.hook.LuckPermsHook
import dev.slne.surf.nametag.paper.listener.NametagListener
import dev.slne.surf.nametag.paper.listener.NametagPacketListener
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        NametagListener.register()
        PacketEvents.getAPI().eventManager.registerListener(NametagPacketListener)

        if (LuckPermsHook.isEnabled()) {
            LuckPermsHook.load()
        }
    }
}