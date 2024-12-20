package org.jesjack.guardGenerator.controllers

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.InventoryHolder
import org.jesjack.guardGenerator.GuardGenerator.Companion.getRegionManager
import org.jesjack.guardGenerator.GuardGenerator.Companion.server

class OnInventoryClickController(private val event: InventoryClickEvent) {
    fun handle() {
        if (event.whoClicked !is Player) return
        if (event.inventory.holder !is InventoryHolder) return
        if (event.inventory.type != InventoryType.CHEST) return
        if (event.inventory.contents.all { it == null }) return

//        check if world name contains player name
        val playerName = event.whoClicked.name
        val worldName = event.whoClicked.world.name
        if (!worldName.contains(playerName)) return
        val x = event.whoClicked.x
        val z = event.whoClicked.z
        val region = protectChunk(x, z, worldName)?: return
        addPlayerToRegion(event.whoClicked as Player, region, event.whoClicked.world)
    }

    private fun protectChunk(x: Double, z: Double, worldName: String): ProtectedCuboidRegion? {
        val world = server.getWorld(worldName) ?: return null
        val chunk = world.getChunkAt(Location(world, x, 0.0, z))
        val chunkX = chunk.x shl 4
        val chunkZ = chunk.z shl 4

        val min = BlockVector3.at(chunkX, 0, chunkZ)
        val max = BlockVector3.at(chunkX + 15, world.maxHeight - 1, chunkZ + 15)

        val regionName = "chunk_${chunk.x}_${chunk.z}"
        // if region already exists, return it
        val regionManager = getRegionManager(world) ?: return null
        if (regionManager.getRegion(regionName) != null) return regionManager.getRegion(regionName) as ProtectedCuboidRegion?

        val region = ProtectedCuboidRegion(regionName, min, max)
        regionManager.addRegion(region)
        return region
    }

    private fun addPlayerToRegion(player: Player, region: ProtectedCuboidRegion, worldOfRegion: World) {
        // if player is already in the region, return
        val regionManager = getRegionManager(worldOfRegion)?: return
        if (region.owners.contains(player.name)) return
        region.owners.addPlayer(player.name)
        regionManager.save()
        player.sendMessage("${region.id} added to your region")
    }
}
