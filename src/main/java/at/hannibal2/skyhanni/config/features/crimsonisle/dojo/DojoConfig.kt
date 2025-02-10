package at.hannibal2.skyhanni.config.features.crimsonisle.dojo;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.Accordion;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class DojoConfig {

    @Expose
    @ConfigOption(name = "Hide Useless Messages", desc = "Hides useless dojo messages, like Master Tao's messages.")
    @ConfigEditorBoolean
    public boolean hideUselessMessages = true;

    @Expose
    @ConfigOption(name = "Test of Force", desc = "")
    @Accordion
    public ForceConfig force = new ForceConfig();

    @Expose
    @ConfigOption(name = "Test of Stamina", desc = "")
    @Accordion
    public StaminaConfig stamina = new StaminaConfig();

    @Expose
    @ConfigOption(name = "Test of Mastery", desc = "")
    @Accordion
    public MasteryConfig mastery = new MasteryConfig();

    @Expose
    @ConfigOption(name = "Test of Discipline", desc = "")
    @Accordion
    public DisciplineConfig discipline = new DisciplineConfig();

    @Expose
    @ConfigOption(name = "Test of Swiftness", desc = "")
    @Accordion
    public SwiftnessConfig swiftness = new SwiftnessConfig();

    @Expose
    @ConfigOption(name = "Test of Control", desc = "")
    @Accordion
    public ControlConfig control = new ControlConfig();

    @Expose
    @ConfigOption(name = "Test of Tenacity", desc = "")
    @Accordion
    public TenacityConfig tenacity = new TenacityConfig();
}
