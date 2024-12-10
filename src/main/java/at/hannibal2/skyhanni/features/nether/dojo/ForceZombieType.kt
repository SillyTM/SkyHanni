package at.hannibal2.skyhanni.features.nether.dojo

import net.minecraft.item.ItemArmor

private typealias Material = ItemArmor.ArmorMaterial

enum class ForceZombieType(
    val points: Int,
    val material: Material?
) {
    IRON(
        10,
        Material.IRON
    ),
    GOLDEN(
        20,
        Material.GOLD
    ),
    DIAMOND(
        30,
        Material.DIAMOND
    ),
    NEGATIVE(
        -30,
        null
    ),
    ;

    companion object {
        fun fromMaterial(material: Material?): ForceZombieType? = entries.find { it.material == material }
    }
}
