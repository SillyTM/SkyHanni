package at.hannibal2.skyhanni.config.features.crimsonisle.dojo;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class TenacityConfig {

    @Expose
    @ConfigOption(name = "Ghast Warning", desc = "Show a warning when a ghast is about to spawn.")
    @ConfigEditorBoolean
    public boolean ghastWarning = false;

    @Expose
    @ConfigOption(name = "Block Highlight", desc = "Highlight the block where the ghast fireball will hit.")
    @ConfigEditorBoolean
    public boolean blockHighlight = true;
}
