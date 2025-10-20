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

import fr.neatmonster.nocheatplus.compat.blocks.changetracker.BlockChangeTracker;

/**
 * Interface for deferred block events that can be safely processed on the main thread.
 * Used to prevent deadlocks on parallel ticking servers like Leaf and Folia.
 */
public interface DeferredBlockEvent {

    /**
     * Process the captured event data by performing block tracking operations.
     * This method will be called on the main thread to avoid deadlocks.
     *
     * @param tracker The BlockChangeTracker to use for adding block changes
     */
    void process(BlockChangeTracker tracker);

    /**
     * Get the name of the world this event occurred in.
     * Used for proper scheduling and world-specific processing.
     *
     * @return The world name
     */
    String getWorldName();
}
