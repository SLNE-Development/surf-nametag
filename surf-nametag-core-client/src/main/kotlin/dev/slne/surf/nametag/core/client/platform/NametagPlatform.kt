package dev.slne.surf.nametag.core.client.platform

import dev.slne.surf.api.core.util.requiredService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.nio.file.Path
import java.util.*

private val platform = requiredService<NametagPlatform>()

/**
 * The platform specific calls the shared nametag logic depends on.
 */
interface NametagPlatform {

    /**
     * The directory this plugin keeps its files in.
     */
    val dataPath: Path

    /**
     * Whether clan tags can be looked up on this server.
     */
    val clanAvailable: Boolean

    /**
     * Whether live tags can be looked up on this server.
     */
    val contentCreatorAvailable: Boolean

    /**
     * Every player currently on this server.
     */
    fun onlinePlayers(): Collection<NametagPlayer>

    /**
     * Returns the player with the given [playerUuid], or `null` if they are not on this server.
     */
    fun player(playerUuid: UUID): NametagPlayer?

    /**
     * Renders a horizontal shift of [amount] pixels as the font glyphs this platform ships.
     */
    fun shift(amount: Int): String

    /**
     * Launches [block] on the scope the platform applies nametag changes on.
     */
    fun launch(block: suspend CoroutineScope.() -> Unit): Job

    companion object : NametagPlatform by platform
}
