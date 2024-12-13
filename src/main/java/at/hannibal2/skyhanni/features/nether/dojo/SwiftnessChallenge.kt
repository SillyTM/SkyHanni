package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.events.LorenzRenderWorldEvent
import at.hannibal2.skyhanni.events.ServerBlockChangeEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.LorenzColor
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.RenderUtils.drawWireframeBoundingBoxNea
import at.hannibal2.skyhanni.utils.RenderUtils.expandBlock
import net.minecraft.init.Blocks
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@SkyHanniModule
object SwiftnessChallenge : DojoChallengeClass(DojoChallenge.SWIFTNESS) {

    private val config get() = DojoAPI.config.swiftness

    private var nextBlock: LorenzVec? = null
    private var previousBlock: LorenzVec? = null

    @SubscribeEvent
    fun onBlockChange(event: ServerBlockChangeEvent) {
        if (!isActive()) return
        val pos = event.location
        if (!DojoAPI.inDojoArena(pos)) return
        if (event.newState.block == Blocks.wool && event.oldState.block == Blocks.air) {
            previousBlock = nextBlock
            nextBlock = pos
        }
    }

    @SubscribeEvent
    fun onRenderWorld(event: LorenzRenderWorldEvent) {
        if (!isActive() || !config.blockHighlight) return
        val pos = nextBlock ?: return
        event.drawWireframeBoundingBoxNea(
            pos.blockBoundingBox().expandBlock(),
            LorenzColor.GREEN.toColor()
        )
    }

    override fun onDebug(builder: MutableList<String>) {
        builder.add("Next block: ${nextBlock?.toCleanString() ?: "None"}")
        builder.add("Previous block: ${previousBlock?.toCleanString() ?: "None"}")
    }

    override fun reset() {
        nextBlock = null
        previousBlock = null
    }
}
