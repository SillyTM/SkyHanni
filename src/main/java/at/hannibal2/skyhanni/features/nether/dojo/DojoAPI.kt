package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.config.features.crimsonisle.dojo.DojoConfig
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.events.DebugDataCollectEvent
import at.hannibal2.skyhanni.events.SecondPassedEvent
import at.hannibal2.skyhanni.events.chat.SkyHanniChatEvent
import at.hannibal2.skyhanni.events.minecraft.WorldChangeEvent
import at.hannibal2.skyhanni.events.skyblock.ScoreboardAreaChangeEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.LocationUtils
import at.hannibal2.skyhanni.utils.LocationUtils.contains
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.NumberUtil.formatInt
import at.hannibal2.skyhanni.utils.RegexUtils.findMatcher
import at.hannibal2.skyhanni.utils.RegexUtils.matchMatcher
import at.hannibal2.skyhanni.utils.StringUtils.removeColor
import at.hannibal2.skyhanni.utils.align
import at.hannibal2.skyhanni.utils.compat.getEntityHelmet
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraft.entity.EntityLiving
import net.minecraft.item.ItemArmor
import net.minecraft.util.AxisAlignedBB

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
        "^§f\\s+(?:§.)*Test of (?<test>.+)\\s",
    )

    /**
     * REGEX-TEST: §f                         §r§6Your Rank: §r§aF §r§8(0) §r§c§lFAILED
     * REGEX-TEST: §f                             §r§6Your Rank: §r§aD §r§8(375)
     */
    private val challengeScorePattern by patternGroup.pattern(
        "challenge.score",
        "§f *§r§6Your Rank: §r§a(?<rank>\\w) §r§8\\((?<score>[\\d,.]+)\\)(?: §r§c§lFAILED)?",
    )

    /**
     * REGEX-TEST: §e[NPC] §eMaster Tao§f: §rI only test people who use their bare skills. No extra help allowed! Come back to me once you've §estored your items away§f.
     * REGEX-TEST: §e[NPC] §eMaster Tao§f: §rAhhh, here we go! Let's get you into the Arena.
     */
    private val taoMessage by patternGroup.pattern(
        "tao",
        "§e\\[NPC] §eMaster Tao§f: (?<message>.*)",
    )

    /**
     * REGEX-TEST: §eThe ghast is becoming more frustrated...
     * REGEX-TEST: §eThe ghasts are becoming more frustrated...
     */
    private val ghastSpawnMessage by patternGroup.pattern(
        "tenacity.ghast",
        "§eThe ghasts? (?:is|are) becoming more frustrated...",
    )

    // todo: only include the important dialogue maybe
    private val taoDialogElleQuest = listOf(
        "Ugo is that you?",
        "You missed yesterday's lesson.",
        "And the one before that.",
        "So today you'll have to do three lessons.",
        "The first one is a Test of Force.",
        "Go in the Arena.",
        "Ahhh, here we go! Let's get you into the Arena.",
        "Onto the next Test, Stamina this time.",
        "One more test before I let you go. The Test of Mastery.",
        "Well done Ugo, you finished your training for today.",
        "Remember Ugo, I can show you the path but I cannot walk it for you.",
        "If you want to make your father proud you will have to train more than it is expected of you.",
        "Respect your training and you will respect yourself.",
        "Go now.",
        "Ugo! You know very well you need to store all your items away before you enter the Arena.",
        "Make sure you empty your inventory entirely before you enter."
    )

    @HandleEvent
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

    private val dojoArenas: List<AxisAlignedBB>
    private var currentArena: AxisAlignedBB? = null

    init {
        val bottomArenaCenter = LorenzVec(-375, 13, -647)
        val mainArenaCenter = LorenzVec(-207, 100, -598)

        val arenaOffset = 47
        val arenaRadius = 22

        val centers = mutableListOf<LorenzVec>()

        for (x in -2..2) {
            for (z in -1..1) {
                val offset = LorenzVec(x, 0, z) * arenaOffset
                val center = bottomArenaCenter + offset
                centers.add(center)
            }
        }
        centers.add(mainArenaCenter)

        dojoArenas = centers.map { center ->
            val corner1 = center - LorenzVec(arenaRadius, 5, arenaRadius)
            val corner2 = center + LorenzVec(arenaRadius, 20, arenaRadius)
            corner1 align corner2
        }
    }


    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onSecondPassed(event: SecondPassedEvent) {
        if (!inChallenge || currentArena != null) return
        val player = LocationUtils.playerLocation()
        currentArena = dojoArenas.firstOrNull { player in it }
    }

    fun inDojoArena(location: LorenzVec): Boolean {
        val arena = currentArena ?: return false
        return location in arena
    }

    @HandleEvent
    fun onAreaChangeEvent(event: ScoreboardAreaChangeEvent) {
        inDojo = event.area == "Dojo" || event.area == "Dojo Arena"
        if (!inDojo) resetDojo()
    }

    @HandleEvent
    fun onWorldChange(event: WorldChangeEvent) {
        inDojo = false
        resetDojo()
    }

    private fun resetDojo() {
        challenge = null
        currentArena = null
        DojoChallengeClass.resetAll()
    }

    private fun tryBlock(event: SkyHanniChatEvent, reason: String) {
        if (config.hideUselessMessages) event.blockedReason = reason
    }

    fun EntityLiving.getHelmetMaterial(): ItemArmor.ArmorMaterial? {
        val helmet = getEntityHelmet() ?: return null
        return (helmet.item as? ItemArmor)?.armorMaterial
    }

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onChat(event: SkyHanniChatEvent) {
        val message = event.message

        challengeStartPattern.findMatcher(message) {
            val name = group("test")
            val newChallenge = DojoChallenge.fromName(name)
            challenge = newChallenge
            return
        }

        if (message == "§cAbilities are disabled in this area!") {
            tryBlock(event, "Dojo")
            return
        }

        ghastSpawnMessage.matchMatcher(event.message) {
            tryBlock(event, "Dojo")
            return
        }

        taoMessage.matchMatcher(message) {
            val taoMessage = group("message").removeColor()
            if ((taoMessage !=
                "I only test people who use their bare skills. No extra help allowed! Come back to me once you've stored your items away.") &&
                !taoDialogElleQuest.contains(taoMessage)) {
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
