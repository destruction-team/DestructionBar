package cc.dstm.destructionBar

import cc.dstm.destructionBar.util.runAsync
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    fun registerBukkitListener(listener: Listener) = server.pluginManager.registerEvents(listener, this)

    val barManager = BarManager(this)
    val barConfigLoader = BarConfigLoader(this)

    override fun onEnable() {

        // Create plugin directory
        if (!dataFolder.exists()) dataFolder.mkdir()

        // Load config
        this.runAsync {
            barConfigLoader.loadFromDisk()
        }

        // Register listeners
        registerBukkitListener(Listener(this))

        // Register commands
        ReloadCommand.register(this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
