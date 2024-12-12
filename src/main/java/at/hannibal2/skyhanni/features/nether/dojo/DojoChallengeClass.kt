package at.hannibal2.skyhanni.features.nether.dojo

abstract class DojoChallengeClass(val challenge: DojoChallenge) {

    protected val patternGroup = DojoAPI.patternGroup.group(challenge.name.lowercase())

    protected fun isActive(): Boolean = challenge.isActive

    protected abstract fun reset()

    protected open fun onDebug(builder: MutableList<String>) {}

    init {
        challenges += this
    }

    companion object {
        private val challenges = mutableListOf<DojoChallengeClass>()

        fun resetAll() = challenges.forEach { it.reset() }
        fun onDebugAll(builder: MutableList<String>) {
            for (challenge in challenges) {
                builder.add(challenge.challenge.displayName)
                challenge.onDebug(builder)
                builder.add("")
            }
        }
    }
}
