package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.events.entity.EntityEnterWorldEvent
import at.hannibal2.skyhanni.events.entity.EntityLeaveWorldEvent
import at.hannibal2.skyhanni.events.minecraft.SkyHanniRenderWorldEvent
import at.hannibal2.skyhanni.events.minecraft.SkyHanniTickEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.BlockUtils
import at.hannibal2.skyhanni.utils.DelayedRun
import at.hannibal2.skyhanni.utils.LorenzColor
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.NumberUtil.ordinal
import at.hannibal2.skyhanni.utils.RenderUtils
import at.hannibal2.skyhanni.utils.RenderUtils.drawFilledBoundingBoxNea
import at.hannibal2.skyhanni.utils.SoundUtils
import at.hannibal2.skyhanni.utils.SoundUtils.playSound
import at.hannibal2.skyhanni.utils.compat.getStandHelmet
import at.hannibal2.skyhanni.utils.getLorenzVec
import at.hannibal2.skyhanni.utils.getMotionLorenzVec
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityGhast
import net.minecraft.init.Blocks
import kotlin.time.Duration.Companion.seconds

@SkyHanniModule
object TenacityChallenge : DojoChallengeClass(DojoChallenge.TENACITY) {

    private val config get() = dojoConfig.tenacity

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
    fun onEntityEnterWorld(event: EntityEnterWorldEvent<EntityLivingBase>) {
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

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onTick(event: SkyHanniTickEvent) {
        if (!isActive()) return
        projectileData.forEach { (entity, data) -> data.update(entity) }
    }

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onWorldRender(event: SkyHanniRenderWorldEvent) {
        if (!isActive()) return
        if (projectileData.isEmpty()) return

        RenderUtils.LineDrawer.draw3D(event.partialTicks) {
            for (data in projectileData.values) {
                val startPos = data.startPos
                var endPos = data.endPos ?: return@draw3D
                //endPos = endPos.copy(x = endPos.x + 0.5,y = endPos.y - 1 ,z = endPos.z + 0.5)

                if (config.projectileLine) {
                    draw3DLine(startPos, endPos.up(0.5), LorenzColor.DARK_RED.toColor(), 5, true)
                }

                if (config.blockHighlight) {
                    drawFilledBoundingBoxNea(
                        endPos.blockBoundingBox().expand(0.99, 0.015, 0.99),
                        LorenzColor.RED.addOpacity(config.opacity)
                    )

                    drawFilledBoundingBoxNea(
                        endPos.blockBoundingBox().expand(2.5, 0.01, 2.5),
                        LorenzColor.GOLD.addOpacity(config.opacity)
                    )
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
