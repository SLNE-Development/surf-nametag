package dev.slne.surf.nametag.core.client.hook

import dev.slne.clan.api.clan.Clan
import dev.slne.clan.api.clan.ClanView
import dev.slne.clan.api.clan.listener.ClanCreatedListener
import dev.slne.clan.api.clan.listener.ClanDeletedListener
import dev.slne.clan.api.clan.listener.ClanUpdateMemberListener
import dev.slne.clan.api.clan.listener.ClanUpdatedListener
import dev.slne.surf.nametag.core.client.platform.NametagPlatform
import dev.slne.surf.nametag.core.client.platform.NametagPlayer
import dev.slne.surf.nametag.core.client.service.NametagService
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import java.util.*

object ClanHook {
    suspend fun getClanTag(playerUuid: UUID): Component =
        Clan.byPlayer(playerUuid)?.renderClanTag(1) ?: Component.empty()

    fun createListeners() {
        Clan.registerListener(ClanCreatedListener(::updateMembers))
        Clan.registerListener(ClanUpdatedListener(::updateMembers))
        Clan.registerListener(ClanDeletedListener(::updateMembers))
        Clan.registerListener(ClanUpdateMemberListener { clan, _, _ -> updateMembers(clan) })
    }

    private fun updateMembers(clan: ClanView) {
        NametagPlatform.launch {
            for (member in clan.members) {
                val player = NametagPlatform.player(member.uuid) ?: continue
                launch { updatePlayer(player) }
            }
        }
    }

    private suspend fun updatePlayer(player: NametagPlayer) {
        NametagService.handleDataUpdate(player)
    }
}
