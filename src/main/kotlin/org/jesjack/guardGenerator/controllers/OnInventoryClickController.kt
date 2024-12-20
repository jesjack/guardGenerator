package org.jesjack.guardGenerator.controllers

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.jesjack.guardGenerator.GuardGenerator.Companion.getRegionManager

class OnInventoryClickController(private val event: InventoryClickEvent) {
    private val regionManager = getRegionManager(event.whoClicked.world)

    fun handle() {
        if (event.whoClicked !is Player
            || event.inventory.type != InventoryType.CHEST
            || event.inventory.contents.all { it == null }
            || !event.whoClicked.world.name.contains(event.whoClicked.name)
        ) return

        addPlayerToRegion(protectChunk())
        event.whoClicked.sendMessage("Region protected!")
    }

    private fun protectChunk(): ProtectedRegion {
        val chunk = event.whoClicked.world.getChunkAt(
            Location(
                event.whoClicked.world,
                event.whoClicked.x,
                0.0,
                event.whoClicked.z
            )
        )
        val chunkX = chunk.x shl 4
        val chunkZ = chunk.z shl 4

        val min = BlockVector3.at(chunkX, 0, chunkZ)
        val max = BlockVector3.at(chunkX + 15, event.whoClicked.world.maxHeight - 1, chunkZ + 15)

        val regionName = "chunk_${chunk.x}_${chunk.z}"
        regionManager?.getRegion(regionName)?.let { return it }

        ProtectedCuboidRegion(regionName, min, max).let {
            regionManager?.addRegion(it)
            return it
        }
    }

    private fun addPlayerToRegion(region: ProtectedRegion) {
        // if player is already in the region, return
        if (region.owners.contains(event.whoClicked.name)) return
        region.owners.addPlayer(event.whoClicked.name)
        regionManager?.save()
    }
}
