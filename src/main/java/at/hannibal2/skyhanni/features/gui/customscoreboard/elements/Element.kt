package at.hannibal2.skyhanni.features.gui.customscoreboard.elements

abstract class Element {
    abstract fun getDisplayPair(): List<Any>
    abstract fun showWhen(): Boolean
    abstract val configLine: String
}
