package at.hannibal2.skyhanni.config.features.crimsonisle.dojo

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ForceConfig {
    @Expose
    @ConfigOption(name = "Highlight Zombies", desc = "Highlight zombies with a color.")
    @ConfigEditorBoolean
    var highlightZombies: Boolean = true

    @Expose
    @ConfigOption(name = "Despawn Timer", desc = "Show timer until the zombie despawns.")
    @ConfigEditorBoolean
    var despawnTimer: Boolean = true
}
