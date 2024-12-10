package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.config.features.crimsonisle.dojo.TenacityConfig
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.NumberUtil.ordinal
import at.hannibal2.skyhanni.utils.SoundUtils
import at.hannibal2.skyhanni.utils.SoundUtils.playSound
import kotlin.time.Duration.Companion.seconds

@SkyHanniModule
object TenacityFeatures {

    private val config = TenacityConfig()

    fun warnGhastSpawn(count: Int) {
        if (!config.ghastWarning) return
        SoundUtils.createSound("random.orb", 0.5f).playSound()
        LorenzUtils.sendTitle("§c${count.ordinal()} Ghast is Spawning", duration = 3.seconds)
    }
}
