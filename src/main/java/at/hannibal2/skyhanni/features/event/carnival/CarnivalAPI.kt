package at.hannibal2.skyhanni.features.event.carnival

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.data.ScoreboardData
import at.hannibal2.skyhanni.events.LorenzChatEvent
import at.hannibal2.skyhanni.events.LorenzRenderWorldEvent
import at.hannibal2.skyhanni.events.mining.OreMinedEvent
import at.hannibal2.skyhanni.features.gui.customscoreboard.ScoreboardPattern
import at.hannibal2.skyhanni.features.mining.OreBlock
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.LocationUtils.isInside
import at.hannibal2.skyhanni.utils.LorenzColor
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.LorenzUtils.isInIsland
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.RegexUtils.firstMatches
import at.hannibal2.skyhanni.utils.RegexUtils.matchMatcher
import at.hannibal2.skyhanni.utils.RegexUtils.matches
import at.hannibal2.skyhanni.utils.RenderUtils.drawColor
import at.hannibal2.skyhanni.utils.RenderUtils.drawString
import at.hannibal2.skyhanni.utils.SimpleTimeMark
import at.hannibal2.skyhanni.utils.StringUtils.removeColor
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraft.util.AxisAlignedBB
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@SkyHanniModule
object CarnivalAPI {

    private val repoGroup = RepoPattern.group("carnival")

    private val fruitGroup = repoGroup.group("fruit")

    /**
     * REGEX-TEST: §c§lMINES! §r§fThere are §r§63 §r§fbombs hidden nearby.
     */
    private val fruitDiggingPattern by fruitGroup.pattern(
        "bombs.nearby",
        "§c§lMINES! §r§fThere are §r§6(?<amount>\\d+) §r§fbombs hidden nearby\\.",
    )

    private val fruitEndPattern by fruitGroup.pattern(
        "end",
        "§f\\s+§r§6§lFruit Digging",
    )

    fun inCarnival() = IslandType.HUB.isInIsland() && LorenzUtils.skyBlockArea == "Carnival"

    val currentTask: String?
        get() = ScoreboardPattern.carnivalTasksPattern.firstMatches(ScoreboardData.sidebarLinesFormatted)?.removeColor()

    private val fruitBounds = AxisAlignedBB(
        -112.0, 72.0, -11.0,
        -106.0, 72.0, -5.0,
    )

    private val fruitBlocks = mutableMapOf<LorenzVec, Int>()

    private var lastDigTime = SimpleTimeMark.farPast()
    private var lastChatTime = SimpleTimeMark.farPast()

    private var lastPos: LorenzVec? = null
    private var lastAmount: Int? = null

    private var timeout = 2.seconds

    @HandleEvent(onlyOnSkyblock = true, onlyOnIsland = IslandType.HUB)
    fun onOreMined(event: OreMinedEvent) {
        if (event.originalOre != OreBlock.FRUIT_SAND) return
        if (currentTask != "Fruit Digging") return
        val pos = event.position
        if (!fruitBounds.isInside(pos)) return

        lastDigTime = SimpleTimeMark.now()
        lastPos = pos
        handleFruitBlock()
    }

    @SubscribeEvent
    fun onChat(event: LorenzChatEvent) {
        if (!inCarnival()) return
        val message = event.message

        fruitDiggingPattern.matchMatcher(message) {
            lastAmount = group("amount")?.toIntOrNull() ?: return
            lastChatTime = SimpleTimeMark.now()
            handleFruitBlock()
            return
        }
        if (fruitEndPattern.matches(message)) {
            lastDigTime = SimpleTimeMark.farPast()
            lastChatTime = SimpleTimeMark.farPast()
            lastPos = null
            lastAmount = null
            fruitBlocks.clear()
        }
    }

    private fun handleFruitBlock() {
        val pos = lastPos?.takeIf { lastDigTime.passedSince() < timeout } ?: return
        val amount = lastAmount?.takeIf { lastChatTime.passedSince() < timeout } ?: return

        fruitBlocks[pos] = amount
        lastDigTime = SimpleTimeMark.farPast()
        lastChatTime = SimpleTimeMark.farPast()
        lastPos = null
        lastAmount = null
    }

    @SubscribeEvent
    fun onRenderWorld(event: LorenzRenderWorldEvent) {
        if (!LorenzUtils.inSkyBlock) return
        if (!inCarnival()) return

        fruitBlocks.forEach { (pos, amount) ->
            event.drawColor(pos, LorenzColor.RED)
            event.drawString(pos.add(0.5, 1.0, 0.5), amount.toString())
        }
    }
}
