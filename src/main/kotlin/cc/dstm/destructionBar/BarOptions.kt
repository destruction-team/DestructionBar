package cc.dstm.destructionBar

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component

data class BarOptions(
    val title: Component?,
    val color: BossBar.Color?,
    val style: BossBar.Overlay?,
    val flags: Set<BossBar.Flag>?
) {

    fun createBar(fallback: BarOptions? = null): BossBar {
        return BossBar.bossBar(
            title ?: fallback?.title ?: Component.empty(),
            0f,
            color ?: fallback?.color ?: BossBar.Color.WHITE,
            style ?: fallback?.style ?: BossBar.Overlay.PROGRESS
        ).flags(flags ?: fallback?.flags ?: setOf<BossBar.Flag>())
    }

}