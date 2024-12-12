package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.utils.ColorUtils.addAlpha
import at.hannibal2.skyhanni.utils.LorenzColor
import net.minecraft.item.ItemArmor

private typealias Material = ItemArmor.ArmorMaterial

enum class ForceZombieType(
    val points: Int,
    val material: Material,
    color: LorenzColor
) {
    IRON(
        10,
        Material.IRON,
        LorenzColor.GRAY,
    ),
    GOLDEN(
        20,
        Material.GOLD,
        LorenzColor.GOLD,
    ),
    DIAMOND(
        30,
        Material.DIAMOND,
        LorenzColor.AQUA,
    ),
    NEGATIVE(
        -30,
        Material.LEATHER,
        LorenzColor.RED,
    ),
    ;

    val color = color.toColor().addAlpha(200)

    companion object {
        fun fromMaterial(material: Material): ForceZombieType? = entries.find { it.material == material }
    }
}
