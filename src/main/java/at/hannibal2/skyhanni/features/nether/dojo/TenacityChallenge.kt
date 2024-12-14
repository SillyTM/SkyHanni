package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.events.LorenzRenderWorldEvent
import at.hannibal2.skyhanni.events.LorenzTickEvent
import at.hannibal2.skyhanni.events.entity.EntityEnterWorldEvent
import at.hannibal2.skyhanni.events.entity.EntityLeaveWorldEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.BlockUtils
import at.hannibal2.skyhanni.utils.DelayedRun
import at.hannibal2.skyhanni.utils.LorenzColor
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.NumberUtil.ordinal
import at.hannibal2.skyhanni.utils.RenderUtils
import at.hannibal2.skyhanni.utils.SoundUtils
import at.hannibal2.skyhanni.utils.SoundUtils.playSound
import at.hannibal2.skyhanni.utils.compat.getStandHelmet
import at.hannibal2.skyhanni.utils.getLorenzVec
import at.hannibal2.skyhanni.utils.getMotionLorenzVec
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityGhast
import net.minecraft.init.Blocks
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@SkyHanniModule
object TenacityChallenge : DojoChallengeClass(DojoChallenge.TENACITY) {

    private val config get() = DojoAPI.config.tenacity

    private data class ProjectileData(
        val startPos: LorenzVec,
        var endPos: LorenzVec? = null,
    ) {
        fun update(entity: EntityArmorStand) {
            val motionVec = entity.getMotionLorenzVec()
            endPos = BlockUtils.rayTrace(startPos, motionVec) ?: (startPos + motionVec)
        }
    }
    private val projectileData = mutableMapOf<EntityArmorStand, ProjectileData>()

    private val warningSound = SoundUtils.createSound("random.orb", 0.5f)

    private var ghastCount: Int = 0

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onEntityEnterWorld(event: EntityEnterWorldEvent<Entity>) {
        if (!isActive()) return
        val entity = event.entity
        if (!DojoAPI.inDojoArena(entity.getLorenzVec())) return
        when (entity) {
            is EntityGhast -> {
                ghastCount++
                warnGhastSpawn(ghastCount)
            }
            is EntityArmorStand -> {
                DelayedRun.runNextTick {
                    val stack = entity.getStandHelmet() ?: return@runNextTick
                    if (stack.item != Blocks.coal_block) return@runNextTick
                    val pos = entity.getLorenzVec().up(1.5)
                    projectileData[entity] = ProjectileData(pos)
                }
            }
        }
    }

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onEntityLeaveWorld(event: EntityLeaveWorldEvent<EntityArmorStand>) {
        projectileData -= event.entity
    }

    private fun warnGhastSpawn(count: Int) {
        if (!config.ghastWarning) return

        warningSound.playSound()
        LorenzUtils.sendTitle("§c$count${count.ordinal()} Ghast is Spawning", duration = 3.seconds)
    }

    @SubscribeEvent
    fun onTick(event: LorenzTickEvent) {
        if (!isActive()) return
        projectileData.forEach { (entity, data) -> data.update(entity) }
    }

    @SubscribeEvent
    fun onWorldRender(event: LorenzRenderWorldEvent) {
        if (!isActive()) return
        if (projectileData.isEmpty()) return

        val drawLine = config.projectileLine

        RenderUtils.LineDrawer.draw3D(event.partialTicks) {
            for (data in projectileData.values) {
                val startPos = data.startPos
                val endPos = data.endPos ?: return@draw3D
                if (drawLine) {
                    draw3DLine(startPos, endPos, LorenzColor.DARK_RED.toColor(), 5, true)
                }
            }
        }
    }

    override fun onDebug(builder: MutableList<String>) {
        builder.add("Ghasts: $ghastCount")
        builder.add("Projectiles: (${projectileData.size})")
        for ((entity, data) in projectileData) {
            builder.add("  - Pos: ${entity.getLorenzVec()}, Start: ${data.startPos}, End: ${data.endPos}")
        }
    }

    override fun reset() {
        projectileData.clear()
        ghastCount = 0
    }
}
