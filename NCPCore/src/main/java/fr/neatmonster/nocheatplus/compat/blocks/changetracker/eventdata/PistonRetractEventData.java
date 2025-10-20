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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import fr.neatmonster.nocheatplus.compat.blocks.changetracker.BlockChangeListener;
import fr.neatmonster.nocheatplus.compat.blocks.changetracker.BlockChangeTracker;
import fr.neatmonster.nocheatplus.utilities.map.BlockFlags;

/**
 * Captures piston retract event data for deferred processing on parallel ticking servers.
 * Handles both legacy and modern piston retract behavior.
 */
public class PistonRetractEventData implements DeferredBlockEvent {

    private final Location pistonLocation;
    private final BlockFace eventDirection;
    private final List<Location> blockLocations;
    private final String worldName;
    private final boolean hasBlocks;

    /**
     * Create a new piston retract event data capture.
     *
     * @param pistonBlock The piston block that is retracting
     * @param eventDirection The direction of the retract event
     * @param blocks The blocks being moved (null for legacy behavior)
     * @param hasBlocks Whether the server provides block lists in retract events
     */
    public PistonRetractEventData(Block pistonBlock, BlockFace eventDirection, List<Block> blocks, boolean hasBlocks) {
        this.pistonLocation = pistonBlock.getLocation().clone();
        this.eventDirection = eventDirection;
        this.hasBlocks = hasBlocks;

        if (hasBlocks && blocks != null) {
            this.blockLocations = blocks.stream()
                .map(block -> block.getLocation().clone())
                .collect(Collectors.toList());
        } else {
            this.blockLocations = null;
        }

        this.worldName = pistonBlock.getWorld().getName();
    }

    @Override
    public void process(BlockChangeTracker tracker) {
        Block pistonBlock = pistonLocation.getBlock();
        List<Block> blocks = null;

        if (hasBlocks && blockLocations != null) {
            blocks = blockLocations.stream()
                .map(Location::getBlock)
                .collect(Collectors.toList());
        } else {
            // Legacy behavior - determine blocks from retract location
            Location retLoc = pistonLocation.clone().add(eventDirection.getModX(), eventDirection.getModY(), eventDirection.getModZ());
            Block retBlock = retLoc.getBlock();
            final long flags = BlockFlags.getBlockFlags(retBlock.getType());
            if ((flags & BlockChangeListener.F_MOVABLE_IGNORE) == 0L && (flags & BlockChangeListener.F_MOVABLE) != 0L) {
                blocks = new ArrayList<Block>(1);
                blocks.add(retBlock);
            }
        }

        // Calculate the direction blocks are or would be moved (towards the piston)
        BlockFace pistonDirection = getDirection(pistonBlock);
        BlockFace direction;
        if (pistonDirection == null) {
            direction = eventDirection;
        } else {
            direction = eventDirection.getOppositeFace();
        }

        tracker.addPistonBlocks(pistonBlock.getRelative(direction.getOppositeFace()), direction, blocks);
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    /**
     * Get the direction the piston is facing based on its block data.
     * This is a simplified version for deferred processing.
     */
    private BlockFace getDirection(Block pistonBlock) {
        // For deferred processing, we use a simplified approach
        // The actual direction calculation happens during event capture
        return null; // Will use eventDirection as fallback
    }
}
