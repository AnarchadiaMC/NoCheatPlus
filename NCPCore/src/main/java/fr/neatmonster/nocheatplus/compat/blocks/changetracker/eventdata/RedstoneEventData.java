/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.neatmonster.nocheatplus.compat.blocks.changetracker.eventdata;

import org.bukkit.Location;
import org.bukkit.block.Block;

import fr.neatmonster.nocheatplus.compat.blocks.changetracker.BlockChangeTracker;

/**
 * Captures redstone event data for deferred processing on parallel ticking servers.
 * Handles redstone power level changes that may trigger block tracking.
 */
public class RedstoneEventData implements DeferredBlockEvent {

    private final Location blockLocation;
    private final String worldName;

    /**
     * Create a new redstone event data capture.
     *
     * @param block The redstone block that changed power level
     */
    public RedstoneEventData(Block block) {
        this.blockLocation = block.getLocation().clone();
        this.worldName = block.getWorld().getName();
    }

    @Override
    public void process(BlockChangeTracker tracker) {
        Block block = blockLocation.getBlock();
        // Add the redstone block with attached potential for doors/switches
        tracker.addBlocks(block); // This will trigger the addBlockWithAttachedPotential logic
    }

    @Override
    public String getWorldName() {
        return worldName;
    }
}
