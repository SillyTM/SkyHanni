package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.config.commands.CommandRegistrationEvent
import at.hannibal2.skyhanni.config.features.crimsonisle.dojo.DojoConfig
import at.hannibal2.skyhanni.events.LorenzChatEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.ChatUtils
import at.hannibal2.skyhanni.utils.RegexUtils.matchMatcher
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@SkyHanniModule
object ChatHider {

    private val config = DojoConfig()
    private val patternGroup = RepoPattern.group("nether.dojo")

    var ghastCounter = 0
    var isInChallenge = false

    private val ghastSpawnMessage = listOf(
        "§eThe ghast is becoming more frustrated...",
        "§eThe ghasts are becoming more frustrated...",
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
        "§e\\[NPC] §eMaster Tao§f:.*"
    )

    //debug
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("challenge") {
            callback { inChallenge() }
        }
    }
    fun inChallenge() {
        ChatUtils.chat("in challenge: §b$isInChallenge") // TODO say what challenge type once detection is added
    }


    @SubscribeEvent
    fun onChat (event: LorenzChatEvent) {
        if (config.hideUselessMessages) {
            if (event.message in ghastSpawnMessage) {
                ghastCounter++
                event.blockedReason = "Dojo"
            }

            taoMessage.matchMatcher(event.message) {
                if (event.message != "§e[NPC] §eMaster Tao§f: §rI only test people who use their bare skills. No extra help allowed! Come back to me once you've §estored your items away§f.") {
                    event.blockedReason = "Master Tao"
                }
            }
        }

        if (event.message == "§e[NPC] §eMaster Tao§f: §rAhhh, here we go! Let's get you into the Arena.") {
            isInChallenge = true
        }
        challengeScorePattern.matchMatcher(event.message) {
            ghastCounter = 0
            //ChatUtils.chat("ghost counter: §b${ghastCounter}")
            isInChallenge = false
        }
    }
}
