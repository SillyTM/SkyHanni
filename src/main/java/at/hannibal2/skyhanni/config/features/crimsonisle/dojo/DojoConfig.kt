package at.hannibal2.skyhanni.config.features.crimsonisle.dojo

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class DojoConfig {
    @Expose
    @ConfigOption(name = "Hide Useless Messages", desc = "Hides useless dojo messages, like Master Tao's messages.")
    @ConfigEditorBoolean
    var hideUselessMessages: Boolean = true

    @Expose
    @ConfigOption(name = "Test of Force", desc = "")
    @Accordion
    var force: ForceConfig = ForceConfig()

    @Expose
    @ConfigOption(name = "Test of Stamina", desc = "")
    @Accordion
    var stamina: StaminaConfig = StaminaConfig()

    @Expose
    @ConfigOption(name = "Test of Mastery", desc = "")
    @Accordion
    var mastery: MasteryConfig = MasteryConfig()

    @Expose
    @ConfigOption(name = "Test of Discipline", desc = "")
    @Accordion
    var discipline: DisciplineConfig = DisciplineConfig()

    @Expose
    @ConfigOption(name = "Test of Swiftness", desc = "")
    @Accordion
    var swiftness: SwiftnessConfig = SwiftnessConfig()

    @Expose
    @ConfigOption(name = "Test of Control", desc = "")
    @Accordion
    var control: ControlConfig = ControlConfig()

    @Expose
    @ConfigOption(name = "Test of Tenacity", desc = "")
    @Accordion
    var tenacity: TenacityConfig = TenacityConfig()
}
