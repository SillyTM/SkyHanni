package at.hannibal2.skyhanni.config.features.crimsonisle.dojo;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class ForceConfig {

    @Expose
    @ConfigOption(name = "Highlight Zombies", desc = "Highlight zombies with a color.")
    @ConfigEditorBoolean
    public boolean highlightZombies = true;

    @Expose
    @ConfigOption(name = "Despawn Timer", desc = "Show timer until the zombie despawns.")
    @ConfigEditorBoolean
    public boolean despawnTimer = true;
}
