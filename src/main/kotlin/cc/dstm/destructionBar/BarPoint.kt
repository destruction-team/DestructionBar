package cc.dstm.destructionBar

import org.bukkit.Location

data class BarPoint(
    val id: String,
    val location: Location,
    val radius: Double,
    val showProgress: Boolean,
    val overrideOptions: BarOptions
)