package cc.dstm.destructionBar

import cc.dstm.destructionBar.util.runAsync
import com.mojang.brigadier.Command
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents

@Suppress("UnstableApiUsage")
object ReloadCommand {

    fun register(plugin: Main) {

        val reloadSubcommand = Commands.literal("reload").executes { ctx ->

            if (!ctx.source.sender.hasPermission("destructionbar.reload")) {
                ctx.source.sender.sendRichMessage("<red>Permission denied")
                return@executes Command.SINGLE_SUCCESS
            }

            plugin.runAsync {
                plugin.barConfigLoader.loadFromDisk()
                ctx.source.sender.sendRichMessage("<green>Reloaded DestructionBar")
            }
            return@executes Command.SINGLE_SUCCESS
        }

        val command = Commands.literal("destructionbar").then(reloadSubcommand).build()

        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
            commands.registrar().register(command)
        }
    }

}