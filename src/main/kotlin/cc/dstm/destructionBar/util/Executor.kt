package cc.dstm.destructionBar.util

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.Callable

private val scheduler = Bukkit.getScheduler()

fun JavaPlugin.runSync(task: Runnable) = scheduler.runTask(this, task)

fun JavaPlugin.runSyncLater(ticks: Long, task: Runnable) = scheduler.runTaskLater(this, task, ticks)

fun JavaPlugin.runAsync(task: Runnable) = scheduler.runTaskAsynchronously(this, task)

fun JavaPlugin.runAsyncLater(ticks: Long, task: Runnable) = scheduler.runTaskLaterAsynchronously(this, task, ticks)

fun JavaPlugin.runTimer(ticksPeriod: Long, task: Runnable) = scheduler.runTaskTimer(this, task, 0, ticksPeriod)

fun JavaPlugin.runTimerAsync(ticksPeriod: Long, task: Runnable) = scheduler.runTaskTimerAsynchronously(this, task, 0, ticksPeriod)

fun <T> JavaPlugin.callSync(task: Callable<T>) = scheduler.callSyncMethod(this, task)