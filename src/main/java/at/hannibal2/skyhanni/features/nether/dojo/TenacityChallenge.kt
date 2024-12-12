package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.events.LorenzChatEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.NumberUtil.ordinal
import at.hannibal2.skyhanni.utils.RegexUtils.matches
import at.hannibal2.skyhanni.utils.SoundUtils
import at.hannibal2.skyhanni.utils.SoundUtils.playSound
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@SkyHanniModule
object TenacityChallenge : DojoChallengeClass(DojoChallenge.TENACITY) {

    private val config get() = DojoAPI.config.tenacity

    /**
     * REGEX-TEST: §eThe ghast is becoming more frustrated...
     * REGEX-TEST: §eThe ghasts are becoming more frustrated...
     */
    private val ghastSpawnPattern by patternGroup.pattern(
        "ghast.spawn",
        "§eThe ghasts? (?:are|is) becoming more frustrated\\.\\.\\."
    )

    var ghastCounter = 0
        private set

    @SubscribeEvent
    fun onChat(event: LorenzChatEvent) {
        if (!isActive()) return
        if (ghastSpawnPattern.matches(event.message)) {
            ghastCounter++
            DojoAPI.tryBlock(event, "Dojo")
            warnGhastSpawn(ghastCounter)
            return
        }
    }

    private fun warnGhastSpawn(count: Int) {
        if (!config.ghastWarning) return
        SoundUtils.createSound("random.orb", 0.5f).playSound()
        LorenzUtils.sendTitle("§c${count.ordinal()} Ghast is Spawning", duration = 3.seconds)
    }

    override fun onDebug(builder: MutableList<String>) {
        builder.add("Ghast Counter: $ghastCounter")
    }

    override fun reset() {
        ghastCounter = 0
    }
}
