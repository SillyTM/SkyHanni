package at.hannibal2.skyhanni.features.nether.dojo.discipline

import at.hannibal2.skyhanni.utils.ColorUtils.addAlpha
import at.hannibal2.skyhanni.utils.LorenzColor
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemArmor.ArmorMaterial
import java.awt.Color

enum class DisciplineZombieType(
    private val armorMaterial: ArmorMaterial,
    private val sword: Item,
    color: Color,
) {
    LEATHER(
        ArmorMaterial.LEATHER,
        Items.wooden_sword,
        Color(0x964B00),
    ),
    IRON(
        ArmorMaterial.IRON,
        Items.iron_sword,
        LorenzColor.GRAY,
    ),
    GOLD(
        ArmorMaterial.GOLD,
        Items.golden_sword,
        LorenzColor.GOLD
    ),
    DIAMOND(
        ArmorMaterial.DIAMOND,
        Items.diamond_sword,
        LorenzColor.AQUA,
    ),
    ;

    constructor(armorMaterial: ArmorMaterial, item: Item, color: LorenzColor) :
        this(armorMaterial, item, color.toColor())

    val color = color.addAlpha(200)

    companion object {
        fun fromArmorMaterial(armorMaterial: ArmorMaterial): DisciplineZombieType? = entries.find { it.armorMaterial == armorMaterial }

        fun fromSword(item: Item): DisciplineZombieType? = entries.find { it.sword == item }
    }
}
