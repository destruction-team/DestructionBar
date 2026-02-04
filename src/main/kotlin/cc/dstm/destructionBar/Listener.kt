package cc.dstm.destructionBar

import cc.dstm.destructionBar.util.runAsync
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

class Listener(val plugin: Main) : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        plugin.runAsync {
            plugin.barManager.updateBar(event.player)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        plugin.runAsync {
            plugin.barManager.updateBar(event.player)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.runAsync {
            plugin.barManager.handlePlayerQuit(event.player)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        plugin.runAsync {
            plugin.barManager.setPoint(event.player, null)
            plugin.barManager.updateBar(event.player)
        }
    }

}