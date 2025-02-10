package at.hannibal2.skyhanni.config.features.crimsonisle.dojo;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class TenacityConfig {

    @Expose
    @ConfigOption(name = "Ghast Warning", desc = "Show a warning when a ghast is about to spawn.")
    @ConfigEditorBoolean
    public boolean ghastWarning = false;

    @Expose
    @ConfigOption(name = "Projectile Path", desc = "Draw a line to show the path of the projectiles.")
    @ConfigEditorBoolean
    public boolean projectileLine = true;

    @Expose
    @ConfigOption(name = "Block Highlight", desc = "Highlight the block where the ghast fireball will hit.")
    @ConfigEditorBoolean
    public boolean blockHighlight = true;

    @Expose
    @ConfigOption(name = "Opacity", desc = "Opacity of the block highlight.")
    @ConfigEditorSlider(minValue = 0, maxValue = 100, minStep = 1)
    public int opacity = 60;
}
