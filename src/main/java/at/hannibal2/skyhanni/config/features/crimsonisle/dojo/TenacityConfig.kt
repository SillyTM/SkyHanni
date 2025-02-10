package at.hannibal2.skyhanni.config.features.crimsonisle.dojo

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class TenacityConfig {
    @Expose
    @ConfigOption(name = "Ghast Warning", desc = "Show a warning when a ghast is about to spawn.")
    @ConfigEditorBoolean
    var ghastWarning: Boolean = false

    @Expose
    @ConfigOption(name = "Projectile Path", desc = "Draw a line to show the path of the projectiles.")
    @ConfigEditorBoolean
    var projectileLine: Boolean = true

    @Expose
    @ConfigOption(name = "Block Highlight", desc = "Highlight the block where the ghast fireball will hit.")
    @ConfigEditorBoolean
    var blockHighlight: Boolean = true

    @Expose
    @ConfigOption(name = "Opacity", desc = "Opacity of the block highlight.")
    @ConfigEditorSlider(minValue = 0f, maxValue = 100f, minStep = 1f)
    var opacity: Int = 60
}
