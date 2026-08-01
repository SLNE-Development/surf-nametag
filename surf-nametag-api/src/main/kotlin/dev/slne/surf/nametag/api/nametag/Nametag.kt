package dev.slne.surf.nametag.api.nametag

import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.Component

data class Nametag(
    val prefix: Component,
    val playerName: String,
    val suffix: Component
)

fun nametag(block: NametagDsl.() -> Unit): Nametag =
    NametagDsl().apply(block).build()

class NametagDsl {
    private var prefix: Component = Component.empty()
    private var playerName: String = ""
    private var suffix: Component = Component.empty()

    fun prefix(block: SurfComponentBuilder.() -> Unit) {
        prefix = SurfComponentBuilder(block)
    }

    fun playerName(name: String) {
        playerName = name
    }

    fun suffix(block: SurfComponentBuilder.() -> Unit) {
        suffix = SurfComponentBuilder(block)
    }

    fun build() = Nametag(
        prefix = prefix,
        playerName = playerName,
        suffix = suffix
    )
}