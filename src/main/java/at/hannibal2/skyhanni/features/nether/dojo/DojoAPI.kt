package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.config.features.crimsonisle.dojo.DojoConfig
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.events.DebugDataCollectEvent
import at.hannibal2.skyhanni.events.LorenzChatEvent
import at.hannibal2.skyhanni.events.LorenzWorldChangeEvent
import at.hannibal2.skyhanni.events.skyblock.ScoreboardAreaChangeEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.LorenzUtils.isInIsland
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.NumberUtil.formatInt
import at.hannibal2.skyhanni.utils.RegexUtils.findMatcher
import at.hannibal2.skyhanni.utils.RegexUtils.matchMatcher
import at.hannibal2.skyhanni.utils.StringUtils.removeColor
import at.hannibal2.skyhanni.utils.align
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@SkyHanniModule
object DojoAPI {

    val config: DojoConfig get() = SkyHanniMod.feature.crimsonIsle.dojo
    val patternGroup = RepoPattern.group("nether.dojo")

    val inChallenge: Boolean get() = challenge != null
    var challenge: DojoChallenge? = null
        private set
    var inDojo = false
        private set

    /**
     * REGEX-TEST: §f                       §r§6Test of Force §r§e§lOBJECTIVES
     */
    private val challengeStartPattern by patternGroup.pattern(
        "challenge.start",
        "^§f\\s+(?:§.)*Test of (?<test>.+)\\s"
    )

    /**
     * REGEX-TEST: §f                         §r§6Your Rank: §r§aF §r§8(0) §r§c§lFAILED
     * REGEX-TEST: §f                             §r§6Your Rank: §r§aD §r§8(375)
     */
    private val challengeScorePattern by patternGroup.pattern(
        "challenge.score",
        "§f *§r§6Your Rank: §r§a(?<rank>\\w) §r§8\\((?<score>[\\d,.]+)\\)(?: §r§c§lFAILED)?"
    )

    /**
     * REGEX-TEST: §e[NPC] §eMaster Tao§f: §rI only test people who use their bare skills. No extra help allowed! Come back to me once you've §estored your items away§f.
     * REGEX-TEST: §e[NPC] §eMaster Tao§f: §rAhhh, here we go! Let's get you into the Arena.
     */
    private val taoMessage by patternGroup.pattern(
        "tao",
        "§e\\[NPC] §eMaster Tao§f: (?<message>.*)"
    )

    @SubscribeEvent
    fun onDebug(event: DebugDataCollectEvent) {
        event.title("DojoAPI")
        if (!inDojo) {
            event.addIrrelevant("Not in Dojo")
            return
        }
        event.addIrrelevant {
            add("In Challenge: $inChallenge")
            add("Current Challenge: ${challenge?.displayName ?: "None"}")
            DojoChallengeClass.onDebugAll(this)
        }
    }

    // TODO: get proper location
    private val dojoArena = LorenzVec(0, 0, 0) align LorenzVec(0, 0, 0)

    fun inDojoArena(location: LorenzVec): Boolean = /*location in dojoArena*/ true

    @HandleEvent
    fun onAreaChangeEvent(event: ScoreboardAreaChangeEvent) {
        inDojo = event.area == "Dojo" || event.area == "Dojo Arena"
        if (!inDojo) resetDojo()
    }

    @SubscribeEvent
    fun onWorldChange(event: LorenzWorldChangeEvent) {
        inDojo = false
        resetDojo()
    }

    private fun resetDojo() {
        challenge = null
        DojoChallengeClass.resetAll()
    }

    fun tryBlock(event: LorenzChatEvent, reason: String) {
        if (config.hideUselessMessages) event.blockedReason = reason
    }

    @SubscribeEvent
    fun onChat(event: LorenzChatEvent) {
        if (!IslandType.CRIMSON_ISLE.isInIsland()) return
        val message = event.message
        challengeStartPattern.findMatcher(message) {
            val name = group("test")
            val newChallenge = DojoChallenge.fromName(name)
            challenge = newChallenge
            return
        }

        taoMessage.matchMatcher(message) {
            val taoMessage = group("message")?.removeColor()
            if (taoMessage != "I only test people who use their bare skills. No extra help allowed! Come back to me once you've stored your items away.") {
                tryBlock(event, "Master Tao")
            }
            return
        }
        challengeScorePattern.matchMatcher(message) {
            val score = group("score").formatInt()
            resetDojo()
            return
        }
    }
}
