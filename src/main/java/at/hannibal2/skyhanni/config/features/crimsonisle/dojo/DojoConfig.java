package at.hannibal2.skyhanni.config.features.crimsonisle.dojo;

import at.hannibal2.skyhanni.config.FeatureToggle;
import at.hannibal2.skyhanni.config.core.config.Position;
import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigLink;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class DojoConfig {

    @ConfigOption(name = "Hide Useless Messages", desc = "Hides usesless dojo messages, like Master Tao's messages.")
    @Expose
    @ConfigEditorBoolean
    public boolean hideUselessMessages = true;
}
