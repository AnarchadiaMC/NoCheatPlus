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

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import fr.neatmonster.nocheatplus.compat.blocks.changetracker.BlockChangeTracker;

/**
 * Captures piston extend event data for deferred processing on parallel ticking servers.
 * Prevents deadlocks by storing immutable snapshots of event data.
 */
public class PistonExtendEventData implements DeferredBlockEvent {

    private final Location pistonLocation;
    private final BlockFace direction;
    private final List<Location> blockLocations;
    private final String worldName;

    /**
     * Create a new piston extend event data capture.
     *
     * @param pistonBlock The piston block that is extending
     * @param direction The direction the piston is extending towards
     * @param blocks The blocks being moved by the piston
     */
    public PistonExtendEventData(Block pistonBlock, BlockFace direction, List<Block> blocks) {
        this.pistonLocation = pistonBlock.getLocation().clone();
        this.direction = direction;
        this.blockLocations = blocks.stream()
            .map(block -> block.getLocation().clone())
            .collect(Collectors.toList());
        this.worldName = pistonBlock.getWorld().getName();
    }

    @Override
    public void process(BlockChangeTracker tracker) {
        Block pistonBlock = pistonLocation.getBlock();
        List<Block> blocks = blockLocations.stream()
            .map(Location::getBlock)
            .collect(Collectors.toList());

        tracker.addPistonBlocks(
            pistonBlock.getRelative(direction),
            direction,
            blocks
        );
    }

    @Override
    public String getWorldName() {
        return worldName;
    }
}
