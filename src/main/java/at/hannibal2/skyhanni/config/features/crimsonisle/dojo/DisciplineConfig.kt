package at.hannibal2.skyhanni.config.features.crimsonisle.dojo;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class DisciplineConfig {

    @Expose
    @ConfigOption(name = "Highlight Zombies", desc = "Highlights zombies in their respective color.")
    @ConfigEditorBoolean
    public boolean highlightZombies = true;

    @Expose
    @ConfigOption(name = "Highlight all", desc = "Highlights all zombies no matter what sword you are holding.")
    @ConfigEditorBoolean
    public boolean highlightAll = true;
}
