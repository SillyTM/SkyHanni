package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.utils.StringUtils.toFormattedName

enum class DojoChallenge {
    FORCE,
    STAMINA,
    MASTERY,
    DISCIPLINE,
    SWIFTNESS,
    CONTROL,
    TENACITY,
    ;

    private val formattedName = toFormattedName()
    val displayName: String get() = "§eTest of $formattedName"

    val isActive: Boolean get() = DojoAPI.challenge == this

    override fun toString(): String = displayName

    companion object {
        fun fromName(name: String): DojoChallenge? = runCatching { valueOf(name.uppercase()) }.getOrNull()
    }
}
