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
 * Captures entity change block event data for deferred processing on parallel ticking servers.
 * Handles blocks changed by entities (e.g., falling sand, endermen, etc.).
 */
public class EntityChangeBlockEventData implements DeferredBlockEvent {

    private final Location blockLocation;
    private final String worldName;

    /**
     * Create a new entity change block event data capture.
     *
     * @param block The block that was changed by an entity
     */
    public EntityChangeBlockEventData(Block block) {
        this.blockLocation = block.getLocation().clone();
        this.worldName = block.getWorld().getName();
    }

    @Override
    public void process(BlockChangeTracker tracker) {
        Block block = blockLocation.getBlock();
        if (block != null) {
            tracker.addBlocks(block); // E.g. falling blocks like sand.
        }
    }

    @Override
    public String getWorldName() {
        return worldName;
    }
}
