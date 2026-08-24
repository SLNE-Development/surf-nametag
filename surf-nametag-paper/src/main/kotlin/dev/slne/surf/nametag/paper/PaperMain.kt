package dev.slne.surf.nametag.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.extensions.pluginManager
import dev.slne.surf.nametag.core.client.config.NametagConfig
import dev.slne.surf.nametag.core.client.hook.ClanHook
import dev.slne.surf.nametag.core.client.hook.ContentCreatorHook
import dev.slne.surf.nametag.paper.hook.registerLuckPermsListeners
import dev.slne.surf.nametag.paper.listener.NametagListener
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {

    @Volatile
    private var hasClanPlugin: Boolean = false

    @Volatile
    private var hasContentCreatorPlugin: Boolean = false


    override fun onEnable() {
        hasClanPlugin = pluginManager.isPluginEnabled(CLAN_PLUGIN)
        hasContentCreatorPlugin = pluginManager.isPluginEnabled(CONTENT_CREATOR_PLUGIN)

        NametagConfig.init()
        NametagListener.register()

        registerLuckPermsListeners()

        if (checkSurfClan()) {
            ClanHook.createListeners()
        }

        if (checkContentCreator()) {
            ContentCreatorHook.registerListener()
        }
    }

    fun checkSurfClan(): Boolean {
        return hasClanPlugin
    }

    fun checkContentCreator(): Boolean {
        return hasContentCreatorPlugin
    }

    private companion object {
        const val CLAN_PLUGIN = "surf-clan-paper"
        const val CONTENT_CREATOR_PLUGIN = "surf-content-creator-paper"
    }
}
