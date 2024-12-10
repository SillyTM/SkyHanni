package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.config.features.crimsonisle.dojo.DojoConfig
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.events.DebugDataCollectEvent
import at.hannibal2.skyhanni.events.LorenzChatEvent
import at.hannibal2.skyhanni.events.LorenzWorldChangeEvent
import at.hannibal2.skyhanni.events.skyblock.ScoreboardAreaChangeEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.LorenzUtils.isInIsland
import at.hannibal2.skyhanni.utils.RegexUtils.matchGroup
import at.hannibal2.skyhanni.utils.RegexUtils.matchMatcher
import at.hannibal2.skyhanni.utils.RegexUtils.matches
import at.hannibal2.skyhanni.utils.StringUtils.removeColor
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@SkyHanniModule
object DojoAPI {

    private val config = DojoConfig()
    private val patternGroup = RepoPattern.group("nether.dojo")

    var ghastCounter = 0
        private set
    var inChallenge = false
        private set
    var challenge: DojoChallenge? = null
        private set
    var inDojo = false
        private set

    /**
     * REGEX-TEST: §eThe ghast is becoming more frustrated...
     * REGEX-TEST: §eThe ghasts are becoming more frustrated...
     */
    private val ghastSpawnPattern by patternGroup.pattern(
        "ghast.spawn",
        "§eThe ghasts? (?:are|is) becoming more frustrated\\.\\.\\."
    )

    /**
     * REGEX-TEST: §f                         §r§6Your Rank: §r§aF §r§8(0) §r§c§lFAILED
     * REGEX-TEST: §f                             §r§6Your Rank: §r§aD §r§8(375)
     */
    private val challengeScorePattern by patternGroup.pattern(
        "challenge.score",
        "§f *§r§6Your Rank: §r§a(?<rank>\\w) §r§8\\(\\d+\\)(?: §r§c§lFAILED)?"
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
            add("inChallenge: $inChallenge")
            add("ghastCounter: $ghastCounter")
        }
    }

    @HandleEvent
    fun onAreaChangeEvent(event: ScoreboardAreaChangeEvent) {
        inDojo = event.area == "Dojo"
        if (!inDojo) reset()
    }

    @SubscribeEvent
    fun onWorldChange(event: LorenzWorldChangeEvent) {
        inDojo = false
        reset()
    }

    private fun reset() {
        ghastCounter = 0
        inChallenge = false
    }

    private fun LorenzChatEvent.tryBlock(reason: String) {
        if (config.hideUselessMessages) blockedReason = reason
    }

    @SubscribeEvent
    fun onChat(event: LorenzChatEvent) {
        if (!IslandType.CRIMSON_ISLE.isInIsland()) return
        val message = event.message
        val taoMessage = taoMessage.matchGroup(message, "message")?.removeColor()
        if (ghastSpawnPattern.matches(message)) {
            ghastCounter++
            event.tryBlock("Dojo")
            return
        }

        if (taoMessage == "I only test people who use their bare skills. No extra help allowed! Come back to me once you've stored your items away.") {
            event.tryBlock("Master Tao")
            return
        }

        if (taoMessage == "Ahhh, here we go! Let's get you into the Arena.") {
            inChallenge = true
            return
        }
        challengeScorePattern.matchMatcher(event.message) {
            reset()
            return
        }
    }
}
