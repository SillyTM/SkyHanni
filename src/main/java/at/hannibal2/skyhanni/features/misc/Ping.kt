package at.hannibal2.skyhanni.features.misc

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.config.commands.CommandRegistrationEvent
import at.hannibal2.skyhanni.events.minecraft.packet.PacketReceivedEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.network.play.server.S01PacketJoinGame
import net.minecraft.network.play.server.S37PacketStatistics
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.round

@SkyHanniModule
object Ping {

    private val mc: Minecraft = Minecraft.getMinecraft()
    private var lastPingAt: Long = -1L
    private var invokedCommand = false
    private var pingCache: Double = -1.0

    var latestPing: Double = 0.0

    @HandleEvent
    fun onCommandRegister(event: CommandRegistrationEvent) {
        event.register("shping") {
            description = "Check your ping"
            callback { sendPing() }
        }
    }

    @HandleEvent
    fun onPacketReceived(event: PacketReceivedEvent) {
        val packet = event.packet
        if (lastPingAt > 0) {
            when (packet) {
                is S01PacketJoinGame -> {
                    lastPingAt = -1L
                    invokedCommand = false
                }

                is S37PacketStatistics -> {
                    val diff = abs(System.nanoTime() - lastPingAt) / 1_000_000.0
                    lastPingAt = -1L
                    pingCache = diff
                    if (invokedCommand) {
                        invokedCommand = false
                        ChatUtils.chat(formatPingMessage(diff))
                    }
                }
            }
        }
    }

    fun startPingUpdater() {
        val scheduler = Executors.newScheduledThreadPool(1)
        ChatUtils.debug("Starting Ping Updater")
        scheduler.scheduleAtFixedRate({
            sendPing()
        }, 0, 5, TimeUnit.SECONDS)
    }

    fun sendPing() {
        if (lastPingAt > 0) {
            if (invokedCommand) {
                ChatUtils.chat("§bAlready pinging!")
                return
            }
        }
        mc.thePlayer.sendQueue.networkManager.sendPacket(
            C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS)
        )
        lastPingAt = System.nanoTime()
        invokedCommand = true
    }

    private fun formatPingMessage(ping: Double): String {
        val color = when {
            ping < 50 -> "2"
            ping < 100 -> "a"
            ping < 149 -> "6"
            ping < 249 -> "c"
            else -> "4"
        }
        latestPing = (round(ping * 100) / 100)
        return "§$color$latestPing §7ms"
    }
}
