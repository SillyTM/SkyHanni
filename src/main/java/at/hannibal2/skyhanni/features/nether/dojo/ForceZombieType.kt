package at.hannibal2.skyhanni.features.nether.dojo

import at.hannibal2.skyhanni.utils.ColorUtils.addAlpha
import at.hannibal2.skyhanni.utils.LorenzColor
import net.minecraft.item.ItemArmor.ArmorMaterial

enum class ForceZombieType(
    val points: Int,
    private val material: ArmorMaterial,
    color: LorenzColor
) {
    IRON(
        10,
        ArmorMaterial.IRON,
        LorenzColor.GRAY,
    ),
    GOLDEN(
        20,
        ArmorMaterial.GOLD,
        LorenzColor.GOLD,
    ),
    DIAMOND(
        30,
        ArmorMaterial.DIAMOND,
        LorenzColor.AQUA,
    ),
    NEGATIVE(
        -30,
        ArmorMaterial.LEATHER,
        LorenzColor.RED,
    ),
    ;

    val color = color.toColor().addAlpha(200)

    companion object {
        fun fromMaterial(material: ArmorMaterial): ForceZombieType? = entries.find { it.material == material }
    }
}
