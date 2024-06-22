package at.hannibal2.skyhanni.features.gui.customscoreboard.events

import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getSbLines
import at.hannibal2.skyhanni.features.misc.ServerRestartTitle
import at.hannibal2.skyhanni.utils.CollectionUtils.addNotNull
import at.hannibal2.skyhanni.utils.RegexUtils.firstMatches

object ServerClose : ScoreboardEvent() {
    override fun getDisplay() = buildList {
        addNotNull(ServerRestartTitle.restartingGreedyPattern.firstMatches(getSbLines())?.split("§8")?.getOrNull(0))
    }

    override val configLine = "§cServer closing soon!"
}
