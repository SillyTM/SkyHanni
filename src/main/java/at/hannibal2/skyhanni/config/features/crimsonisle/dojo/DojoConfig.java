package at.hannibal2.skyhanni.config.features.crimsonisle.dojo;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.Accordion;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class DojoConfig {

    @ConfigOption(name = "Hide Useless Messages", desc = "Hides usesless dojo messages, like Master Tao's messages.")
    @Expose
    @ConfigEditorBoolean
    public boolean hideUselessMessages = true;

    @ConfigOption(name = "Test of Force", desc = "")
    @Accordion
    @Expose
    public ForceConfig force = new ForceConfig();

    @ConfigOption(name = "Test of Stamina", desc = "")
    @Accordion
    @Expose
    public StaminaConfig stamina = new StaminaConfig();

    @ConfigOption(name = "Test of Mastery", desc = "")
    @Accordion
    @Expose
    public MasteryConfig mastery = new MasteryConfig();

    @ConfigOption(name = "Test of Discipline", desc = "")
    @Accordion
    @Expose
    public DisciplineConfig discipline = new DisciplineConfig();

    @ConfigOption(name = "Test of Swiftness", desc = "")
    @Accordion
    @Expose
    public SwiftnessConfig swiftness = new SwiftnessConfig();

    @ConfigOption(name = "Test of Control", desc = "")
    @Accordion
    @Expose
    public ControlConfig control = new ControlConfig();

    @ConfigOption(name = "Test of Tenacity", desc = "")
    @Accordion
    @Expose
    public TenacityConfig tenacity = new TenacityConfig();
}
