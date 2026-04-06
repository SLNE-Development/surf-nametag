package dev.slne.surf.nametag.paper.hook

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.clan.api.clan.Clan
import dev.slne.clan.api.clan.ClanView
import dev.slne.clan.api.clan.listener.ClanCreatedListener
import dev.slne.clan.api.clan.listener.ClanDeletedListener
import dev.slne.clan.api.clan.listener.ClanUpdateMemberListener
import dev.slne.clan.api.clan.listener.ClanUpdatedListener
import dev.slne.surf.nametag.paper.plugin
import dev.slne.surf.nametag.paper.service.nametagService
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object ClanHook {
    suspend fun getClanTag(playerUuid: UUID): Component? =
        Clan.byPlayer(playerUuid)?.renderClanTag(1)

    fun createListeners() {
        Clan.registerListener(object : ClanCreatedListener {
            override fun onClanCreated(clan: Clan) {
                plugin.launch {
                    clan.members.map { member -> member.uuid }.mapNotNull { Bukkit.getPlayer(it) }
                        .forEach(::updatePlayer)
                }
            }
        })

        Clan.registerListener(object : ClanUpdatedListener {
            override fun onClanUpdated(clan: Clan) {
                plugin.launch {
                    clan.members.map { member -> member.uuid }.mapNotNull { Bukkit.getPlayer(it) }
                        .forEach(::updatePlayer)
                }
            }
        })

        Clan.registerListener(object : ClanUpdateMemberListener {
            override fun onClanMemberUpdated(clan: Clan, memberUuid: UUID, added: Boolean) {
                plugin.launch {
                    clan.members.map { member -> member.uuid }.mapNotNull { Bukkit.getPlayer(it) }
                        .forEach(::updatePlayer)
                }
            }
        })

        Clan.registerListener(object : ClanDeletedListener {
            override fun onClanDeleted(clan: ClanView) {
                plugin.launch {
                    clan.members.map { member -> member.uuid }.mapNotNull { Bukkit.getPlayer(it) }
                        .forEach(::updatePlayer)
                }
            }
        })
    }


    private fun updatePlayer(player: Player) {
        nametagService.handleDataUpdate(player)
    }
}