package at.hannibal2.skyhanni.features.nether.dojo

abstract class DojoChallengeClass(val challenge: DojoChallenge) {

    protected inline val dojoConfig get() = DojoAPI.config

    protected val patternGroup = DojoAPI.patternGroup.group(challenge.name.lowercase())

    protected fun isActive(): Boolean = challenge.isActive

    protected abstract fun reset()

    protected open fun onDebug(builder: MutableList<String>) {}

    init {
        @Suppress("LeakingThis")
        challenges += this
    }

    companion object {
        private val challenges = mutableListOf<DojoChallengeClass>()

        fun resetAll() = challenges.forEach { it.reset() }
        fun onDebugAll(builder: MutableList<String>) {
            for ((index, challenge) in challenges.withIndex()) {
                if (index != 0) builder.add("")
                builder.add(challenge.challenge.displayName)
                challenge.onDebug(builder)
            }
        }
    }
}
