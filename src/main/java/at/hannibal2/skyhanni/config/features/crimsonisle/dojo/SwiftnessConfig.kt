package at.hannibal2.skyhanni.config.features.crimsonisle.dojo;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class SwiftnessConfig {

    @Expose
    @ConfigOption(name = "Highlight Block", desc = "Highlights the next block you have to stand on.")
    @ConfigEditorBoolean
    public boolean blockHighlight = true;

    @Expose
    @ConfigOption(name = "Draw Line", desc = "Draws a line to the next block from the previous block.")
    @ConfigEditorBoolean
    public boolean drawLine = false;

}
