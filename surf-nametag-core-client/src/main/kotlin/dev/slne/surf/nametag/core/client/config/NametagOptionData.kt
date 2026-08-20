package dev.slne.surf.nametag.core.client.config

/**
 * The friendly flags a nametag team is created with.
 *
 * @property friendlyFlags the bit mask the protocol expects for this option
 */
enum class NametagOptionData(val friendlyFlags: Byte) {
    NONE(0),
    FRIENDLY_FIRE(1),
    FRIENDLY_CAN_SEE_INVISIBLE(2),
    ALL(3)
}
