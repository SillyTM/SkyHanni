package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.events.ItemInHandChangeEvent
import at.hannibal2.skyhanni.events.entity.EntityLeaveWorldEvent
import at.hannibal2.skyhanni.events.entity.EntityMaxHealthUpdateEvent
import at.hannibal2.skyhanni.features.nether.dojo.DojoAPI.getHelmetMaterial
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.EntityUtils.highlight
import at.hannibal2.skyhanni.utils.ItemUtils.name
import at.hannibal2.skyhanni.utils.NeuItems.getItemStackOrNull
import at.hannibal2.skyhanni.utils.compat.getEntityHelmet
import at.hannibal2.skyhanni.utils.getLorenzVec
import net.minecraft.entity.monster.EntityZombie

@SkyHanniModule
object DisciplineChallenge : DojoChallengeClass(DojoChallenge.DISCIPLINE) {

    private val config get() = DojoAPI.config.discipline

    private val zombies = mutableMapOf<EntityZombie, DisciplineZombieType>()
    private var heldType: DisciplineZombieType? = null

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onEntityHealthUpdate(event: EntityMaxHealthUpdateEvent) {
        if (!isActive()) return
        val entity = event.entity as? EntityZombie ?: return
        if (!DojoAPI.inDojoArena(entity.getLorenzVec())) return
        val material = entity.getHelmetMaterial() ?: return
        val type = DisciplineZombieType.fromArmorMaterial(material) ?: return
        entity.highlight(type.color) {
            if (!config.highlightZombies) return@highlight false
            return@highlight config.highlightAll || type == heldType
        }
        zombies[entity] = type
    }

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onEntityLeave(event: EntityLeaveWorldEvent<EntityZombie>) {
        zombies -= event.entity
    }

    @HandleEvent(onlyOnIsland = IslandType.CRIMSON_ISLE)
    fun onItemHeldChange(event: ItemInHandChangeEvent) {
        if (!isActive()) return
        val item = event.newItem.getItemStackOrNull()?.item ?: return resetHeldItem()
        heldType = DisciplineZombieType.fromSword(item) ?: return resetHeldItem()
    }

    private fun resetHeldItem() {
        heldType = null
    }

    override fun onDebug(builder: MutableList<String>) {
        builder.add("Held type: ${heldType?.name ?: "None"}")
        builder.add("Zombies:")
        zombies.forEach { (entity, type) ->
            builder.add("  - ${entity.getEntityHelmet()?.name ?: "No Material"} (${type.name})")
        }
    }

    override fun reset() {
        zombies.clear()
        heldType = null
    }

}
