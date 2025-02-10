package at.hannibal2.skyhanni.config.features.crimsonisle.dojo

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class DisciplineConfig {
    @Expose
    @ConfigOption(name = "Highlight Zombies", desc = "Highlights zombies in their respective color.")
    @ConfigEditorBoolean
    var highlightZombies: Boolean = true

    @Expose
    @ConfigOption(name = "Highlight all", desc = "Highlights all zombies no matter what sword you are holding.")
    @ConfigEditorBoolean
    var highlightAll: Boolean = true
}
