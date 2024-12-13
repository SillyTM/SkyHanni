package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.events.LorenzRenderWorldEvent
import at.hannibal2.skyhanni.events.entity.EntityEnterWorldEvent
import at.hannibal2.skyhanni.events.entity.EntityLeaveWorldEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.BlockUtils
import at.hannibal2.skyhanni.utils.ItemUtils.name
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.NumberUtil.ordinal
import at.hannibal2.skyhanni.utils.RenderUtils
import at.hannibal2.skyhanni.utils.SoundUtils
import at.hannibal2.skyhanni.utils.SoundUtils.playSound
import at.hannibal2.skyhanni.utils.getMotionLorenzVec
import at.hannibal2.skyhanni.utils.toLorenzVec
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityGhast
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import kotlin.time.Duration.Companion.seconds

@SkyHanniModule
object TenacityChallenge : DojoChallengeClass(DojoChallenge.TENACITY) {

    private val config get() = DojoAPI.config.tenacity

    data class start_pos(val pos: LorenzVec)
    private var projectileData: MutableMap<EntityArmorStand, start_pos> = mutableMapOf()

    private var ghastCount: Int = 0

    @HandleEvent
    fun onEntityEnterWorld(event: EntityEnterWorldEvent<Entity>) {
        when (val entity = event.entity) {
            is EntityGhast -> {
                ghastCount++
                warnGhastSpawn(ghastCount)
            }
            is EntityArmorStand -> {
                val (_, stack) = entity.inventory.withIndex().firstOrNull { it.value != null } ?: return
                if (stack.name != "Block of Coal") return

                projectileData += mapOf(entity to start_pos(entity.position.toLorenzVec()))
            }
        }
    }

    @HandleEvent
    fun onEntityLeaveWorld(event: EntityLeaveWorldEvent<EntityArmorStand>) {
        projectileData -= event.entity
    }

    private fun warnGhastSpawn(count: Int) {
        if (!config.ghastWarning) return

        SoundUtils.createSound("random.orb", 0.5f).playSound()
        LorenzUtils.sendTitle("§c$count${count.ordinal()} Ghast is Spawning", duration = 3.seconds)
    }

    @SubscribeEvent
    fun onWorldRender(event: LorenzRenderWorldEvent) {
        if (!LorenzUtils.inSkyBlock) return
        if (projectileData.isEmpty()) return

        RenderUtils.LineDrawer.draw3D(event.partialTicks) {
            for ((entity, startPos) in projectileData) {
                val motionVec = entity.getMotionLorenzVec()
                val endPos = BlockUtils.rayTrace(startPos.pos, motionVec) ?: (startPos.pos + motionVec)
                draw3DLine(startPos.pos, endPos, Color.GREEN, 5, true)
            }
        }
    }

    override fun reset() {
        projectileData.clear()
        ghastCount = 0
    }
}
