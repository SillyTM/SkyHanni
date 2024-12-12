package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.events.EntityMaxHealthUpdateEvent
import at.hannibal2.skyhanni.events.LorenzRenderWorldEvent
import at.hannibal2.skyhanni.events.entity.EntityLeaveWorldEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.EntityUtils.highlight
import at.hannibal2.skyhanni.utils.RenderUtils.drawString
import at.hannibal2.skyhanni.utils.SimpleTimeMark
import at.hannibal2.skyhanni.utils.TimeUtils.format
import at.hannibal2.skyhanni.utils.compat.getEntityHelmet
import at.hannibal2.skyhanni.utils.getLorenzVec
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.item.ItemArmor
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@SkyHanniModule
object ForceChallenge : DojoChallengeClass(DojoChallenge.FORCE) {

    private data class ZombieData(
        val type: ForceZombieType,
        val time: SimpleTimeMark = SimpleTimeMark.now() + DESPAWN_TIME,
    )

    private val config get() = DojoAPI.config.force
    private val DESPAWN_TIME = 10.seconds

    private val forceZombies = mutableMapOf<EntityZombie, ZombieData>()

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onHealthUpdate(event: EntityMaxHealthUpdateEvent) {
        if (!isActive()) return
        val entity = event.entity as? EntityZombie ?: return
        if (!DojoAPI.inDojoArena(entity.getLorenzVec())) return
        val material = entity.getHelmetMaterial() ?: return
        val type = ForceZombieType.fromMaterial(material) ?: return
        entity.highlight(type.color, config::highlightZombies)
        forceZombies[entity] = ZombieData(type)
    }

    @SubscribeEvent
    fun onRenderWorld(event: LorenzRenderWorldEvent) {
        if (!isActive() || !config.despawnTimer) return
        forceZombies.forEach { (entity, data) ->
            val (type, time) = data
            val timeLeft = time.timeUntil()
            val pos = entity.getLorenzVec()
            event.drawString(pos.up(2), timeLeft.format(showMilliSeconds = true), color = type.color)
        }
    }

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onEntityLeaveWorld(event: EntityLeaveWorldEvent<EntityZombie>) {
        forceZombies -= event.entity
    }

    override fun onDebug(builder: MutableList<String>) {
        builder.add("Zombies: ${forceZombies.size}")
        forceZombies.forEach { (entity, data) ->
            builder.add("  - Material: ${entity.getHelmetMaterial()}, Type: ${data.type}")
        }
    }

    override fun reset() {
        forceZombies.clear()
    }

    private fun EntityZombie.getHelmetMaterial(): ItemArmor.ArmorMaterial? {
        val helmet = getEntityHelmet() ?: return null
        return (helmet.item as? ItemArmor)?.armorMaterial
    }


}
