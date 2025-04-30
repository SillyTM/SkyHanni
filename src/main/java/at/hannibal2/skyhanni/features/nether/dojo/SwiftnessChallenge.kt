package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.events.ServerBlockChangeEvent
import at.hannibal2.skyhanni.events.minecraft.SkyHanniRenderWorldEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.LorenzColor
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.RenderUtils.draw3DLine
import at.hannibal2.skyhanni.utils.RenderUtils.drawWireframeBoundingBox
import at.hannibal2.skyhanni.utils.RenderUtils.expandBlock
import net.minecraft.init.Blocks

@SkyHanniModule
object SwiftnessChallenge : DojoChallengeClass(DojoChallenge.SWIFTNESS) {

    private val config get() = dojoConfig.swiftness

    private var nextBlock: LorenzVec? = null
    private var previousBlock: LorenzVec? = null

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onBlockChange(event: ServerBlockChangeEvent) {
        if (!isActive()) return
        val pos = event.location
        if (!DojoAPI.inDojoArena(pos)) return
        if (event.newState.block == Blocks.wool && event.oldState.block == Blocks.air) {
            previousBlock = nextBlock
            nextBlock = pos
        }
    }

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onRenderWorld(event: SkyHanniRenderWorldEvent) {
        if (!isActive()) return
        val pos = nextBlock ?: return
        if (config.blockHighlight) {
            event.drawWireframeBoundingBox(
                pos.blockBoundingBox().expandBlock(),
                LorenzColor.GREEN.toColor()
            )
        }
        if (config.drawLine) {
            val prev = previousBlock ?: return
            val prevCenter = prev.centerTopFace()
            val posCenter = pos.centerTopFace()
            event.draw3DLine(
                prevCenter,
                posCenter,
                LorenzColor.AQUA.toColor(),
                3,
                false
            )
        }
    }

    private fun LorenzVec.centerTopFace() = blockCenter().up(0.51)

    override fun onDebug(builder: MutableList<String>) {
        builder.add("Next block: ${nextBlock?.toCleanString() ?: "None"}")
        builder.add("Previous block: ${previousBlock?.toCleanString() ?: "None"}")
    }

    override fun reset() {
        nextBlock = null
        previousBlock = null
    }
}
