package cc.dstm.destructionBar

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class BarManager(val plugin: Main) {

    var defaultBar: BossBar? = null; private set
    var defaultBarOptions: BarOptions? = null
        set(newBarOptions) {

            defaultBar?.let { oldBar -> oldBar.viewers().forEach { oldBar.removeViewer(it as Audience) } }
            defaultBar = newBarOptions?.createBar()

            field = newBarOptions
        }

    var barPoints: List<BarPoint> = listOf()
        set(newBarPoints) {
            field = newBarPoints
            plugin.server.onlinePlayers.forEach { updateBar(it) }
        }

    data class PointedBossBar(val barPoint: BarPoint, val bossBar: BossBar)
    val pointedBossBars: MutableMap<UUID, PointedBossBar> = ConcurrentHashMap()

    fun getBarPoint(location: Location): Pair<BarPoint, Double>? {
        var best: Pair<BarPoint, Double>? = null
        for (barPoint in barPoints) {
            if (barPoint.location.world != location.world) continue
            val distance = barPoint.location.distance(location)
            if (distance <= barPoint.radius && (best == null || distance < best.second)) {
                best = barPoint to distance
            }
        }
        return best
    }

    // Чтобы одновременно не проделывать две операции с поинтед барами для одного игрока
    private val locks = ConcurrentHashMap<UUID, Any>()
    fun <T> withPlayerLock(player: Player, block: () -> T): T {
        val lock = locks.computeIfAbsent(player.uniqueId) { Any() }
        return synchronized(lock) {
            block()
        }
    }

    fun updateBar(player: Player) {
        withPlayerLock(player) {

            val currentBarPoint = getBarPoint(player.location)
            val oldPointedBossBar = pointedBossBars[player.uniqueId]

            if (currentBarPoint != null) {
                if (oldPointedBossBar != null) {
                    if (currentBarPoint.first.id == oldPointedBossBar.barPoint.id) {
                        // Перемещение у той же точки
                        // Обновляем прогресс
                        oldPointedBossBar.bossBar.progress(1 - (currentBarPoint.second / currentBarPoint.first.radius).toFloat())
                    } else {
                        // Перешёл из одной точки в другую
                        // Устанавливаем для игрока новую точку
                        setPoint(player, currentBarPoint.first)
                    }
                } else {
                    // Вошёл в точку
                    // Устанавливаем для игрока новую точку
                    setPoint(player, currentBarPoint.first)
                }
            } else {
                if (oldPointedBossBar != null) {
                    // Вышёл из точки
                    // Убираем точку совсем
                    setPoint(player, null)
                }
                // Если двигался вне точек - пофиг (дефолт бар добавляется при заходе)
            }
        }
    }

    fun setPoint(player: Player, newPoint: BarPoint?) {
        withPlayerLock(player) {
            // Удалить старое, если есть
            val oldPointedBossBar = pointedBossBars[player.uniqueId]
            oldPointedBossBar?.bossBar?.removeViewer(player)
            pointedBossBars.remove(player.uniqueId)

            if (newPoint != null) {
                val newPointedBossBar = PointedBossBar(
                    newPoint,
                    newPoint.overrideOptions.createBar(defaultBarOptions)
                )
                newPointedBossBar.bossBar.addViewer(player)
                pointedBossBars[player.uniqueId] = newPointedBossBar
                defaultBar?.removeViewer(player)
            }
            else {
                defaultBar?.addViewer(player)
            }
        }
    }

    fun handlePlayerQuit(player: Player) {

        val pointedBossBar = pointedBossBars[player.uniqueId]

        if (pointedBossBar != null) {
            pointedBossBar.bossBar.removeViewer(player)
            pointedBossBars.remove(player.uniqueId)
        }
        else {
            defaultBar?.removeViewer(player)
        }

        locks.remove(player.uniqueId)

    }

}