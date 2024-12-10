package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.config.features.crimsonisle.dojo.DojoConfig
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.events.DebugDataCollectEvent
import at.hannibal2.skyhanni.events.EntityMaxHealthUpdateEvent
import at.hannibal2.skyhanni.events.LorenzChatEvent
import at.hannibal2.skyhanni.events.LorenzWorldChangeEvent
import at.hannibal2.skyhanni.events.entity.EntityLeaveWorldEvent
import at.hannibal2.skyhanni.events.skyblock.ScoreboardAreaChangeEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.LocationUtils.contains
import at.hannibal2.skyhanni.utils.LorenzUtils.isInIsland
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.RegexUtils.findMatcher
import at.hannibal2.skyhanni.utils.RegexUtils.matchMatcher
import at.hannibal2.skyhanni.utils.RegexUtils.matches
import at.hannibal2.skyhanni.utils.StringUtils.removeColor
import at.hannibal2.skyhanni.utils.align
import at.hannibal2.skyhanni.utils.compat.getEntityHelmet
import at.hannibal2.skyhanni.utils.getLorenzVec
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemArmor.ArmorMaterial
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@SkyHanniModule
object DojoAPI {

    private val config = DojoConfig()
    private val patternGroup = RepoPattern.group("nether.dojo")

    var ghastCounter = 0
        private set
    val inChallenge: Boolean get() = challenge != null
    var challenge: DojoChallenge? = null
        private set
    var inDojo = false
        private set
    private val forceZombies = mutableMapOf<EntityZombie, ForceZombieType>()

    /**
     * REGEX-TEST: §eThe ghast is becoming more frustrated...
     * REGEX-TEST: §eThe ghasts are becoming more frustrated...
     */
    private val ghastSpawnPattern by patternGroup.pattern(
        "ghast.spawn",
        "§eThe ghasts? (?:are|is) becoming more frustrated\\.\\.\\."
    )

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
            add("challenge: ${challenge?.name}")
            add("ghastCounter: $ghastCounter")
        }
    }

    // TODO: get proper location
    private val dojoArena = LorenzVec(0, 0, 0) align LorenzVec(0, 0, 0)

    private fun LorenzVec.inDojoArena(): Boolean = this in dojoArena

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onHealthUpdate(event: EntityMaxHealthUpdateEvent) {
        if (!DojoChallenge.FORCE.isActive) return
        val entity = event.entity as? EntityZombie ?: return
        if (!entity.getLorenzVec().inDojoArena()) return
        val helmet = entity.getEntityHelmet()?.item?.let { it as? ItemArmor }
        val type = ForceZombieType.fromMaterial(helmet?.armorMaterial) ?: return
        forceZombies[entity] = type
    }

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onEntityLeaveWorld(event: EntityLeaveWorldEvent<EntityZombie>) {
        forceZombies -= event.entity
    }

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
        ghastCounter = 0
    }

    private fun LorenzChatEvent.tryBlock(reason: String) {
        if (config.hideUselessMessages) blockedReason = reason
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
        when (challenge) {
            DojoChallenge.TENACITY -> {
                if (ghastSpawnPattern.matches(message)) {
                    ghastCounter++
                    event.tryBlock("Dojo")
                    return
                }
            }
            else -> {}
        }

        taoMessage.matchMatcher(message) {
            val taoMessage = group("message")?.removeColor()
            if (taoMessage == "I only test people who use their bare skills. No extra help allowed! Come back to me once you've stored your items away.") {
                event.tryBlock("Master Tao")
            }
            return
        }
        challengeScorePattern.matchMatcher(event.message) {
            resetDojo()
            return
        }
    }
}
