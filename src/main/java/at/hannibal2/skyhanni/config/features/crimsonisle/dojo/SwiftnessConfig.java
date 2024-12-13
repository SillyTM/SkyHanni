package at.hannibal2.skyhanni.config.features.crimsonisle.dojo;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class SwiftnessConfig {

    @Expose
    @ConfigOption(name = "Highlight Block", desc = "highlights the next block you have to stand on")
    @ConfigEditorBoolean
    public boolean blockHighlight = false;

}
