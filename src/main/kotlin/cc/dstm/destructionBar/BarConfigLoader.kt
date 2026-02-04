package cc.dstm.destructionBar

import net.kyori.adventure.bossbar.BossBar
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

class BarConfigLoader(val plugin: Main) {

    var config = YamlConfiguration(); private set
    val configFileName = "config.yml"
    private val configFile = File(plugin.dataFolder, configFileName)

    fun createFile() {
        if (!configFile.exists()) {
            plugin.saveResource(configFileName, false)
        }
    }

    fun loadFromDisk() {
        createFile()
        try {
            config.load(configFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val keys = config.getKeys(false)

        if (keys.contains("default")) {
            keys.remove("default")
            val section = config.getConfigurationSection("default")!!
            plugin.barManager.defaultBarOptions = readBarOptions(section)
        }
        else {
            plugin.barManager.defaultBarOptions = null
        }

        val barPoints = mutableListOf<BarPoint>()
        for (key in keys) {

            val section = config.getConfigurationSection(key)!!
            barPoints.add(readBarPoint(key, section))
        }
        plugin.barManager.barPoints = barPoints
    }

    fun readBarOptions(section: ConfigurationSection): BarOptions {
         return BarOptions(
             title = section.getRichMessage("title"),
             color = section.getString("color")?.let { BossBar.Color.valueOf(it) },
             style = section.getString("style")?.let { BossBar.Overlay.valueOf(it) },
             flags = section.getStringList("flags").takeIf { section.contains("flags") }?.map { BossBar.Flag.valueOf(it) }?.toSet()
         )
    }

    fun readBarPoint(key: String, section: ConfigurationSection): BarPoint {
        return BarPoint(
            id = key,
            location = section.getLocation("location") ?: error("Invalid point bar location"),
            radius = section.getDouble("radius"),
            showProgress = section.getBoolean("show-progress"),
            overrideOptions = readBarOptions(section)
        )
    }

}