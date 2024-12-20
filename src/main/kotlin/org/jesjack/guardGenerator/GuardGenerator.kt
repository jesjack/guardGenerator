package org.jesjack.guardGenerator

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.managers.RegionManager
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin
import org.jesjack.guardGenerator.controllers.OnInventoryClickController
import java.util.logging.Logger

class GuardGenerator : JavaPlugin(), Listener {

    companion object {
        lateinit var worldGuardPlugin: WorldGuardPlugin
        lateinit var logger: Logger
        lateinit var server: Server

        fun getRegionManager(world: World): RegionManager? {
            return WorldGuard.getInstance().platform.regionContainer.get(BukkitAdapter.adapt(world))
        }
    }

    override fun onEnable() {
        // Plugin startup logic
        GuardGenerator.logger = logger
        GuardGenerator.server = server
        server.pluginManager.registerEvents(this, this)
        worldGuardPlugin = server.pluginManager.getPlugin("WorldGuard") as? WorldGuardPlugin
            ?: run {
                logger.severe("WorldGuard not found. GuardGenerator will not function.")
                server.pluginManager.disablePlugin(this)
                return@onEnable
            }
        logger.info("GuardGenerator enabled.")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("GuardGenerator disabled.")
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        server.scheduler.runTaskLater(this, Runnable {
            OnInventoryClickController(event).handle()
        }, 1L) // 1 tick de retraso
    }
}
