package at.hannibal2.skyhanni.config.features.crimsonisle.dojo

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class SwiftnessConfig {
    @Expose
    @ConfigOption(name = "Highlight Block", desc = "Highlights the next block you have to stand on.")
    @ConfigEditorBoolean
    var blockHighlight: Boolean = true

    @Expose
    @ConfigOption(name = "Draw Line", desc = "Draws a line to the next block from the previous block.")
    @ConfigEditorBoolean
    var drawLine: Boolean = false
}
