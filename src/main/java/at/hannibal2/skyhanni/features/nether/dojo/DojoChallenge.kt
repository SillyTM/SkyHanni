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

    private val displayName = toFormattedName()
    val testName: String get() = "§eTest of $displayName"

    val isActive: Boolean get() = DojoAPI.challenge == this

    override fun toString(): String = displayName
}
