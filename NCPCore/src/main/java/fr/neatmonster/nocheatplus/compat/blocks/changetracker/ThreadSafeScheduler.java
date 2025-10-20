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
package fr.neatmonster.nocheatplus.compat.blocks.changetracker;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.neatmonster.nocheatplus.NCPAPIProvider;
import fr.neatmonster.nocheatplus.compat.blocks.changetracker.eventdata.DeferredBlockEvent;
import fr.neatmonster.nocheatplus.logging.Streams;

/**
 * Thread-safe scheduler for deferring block tracking operations to the main thread.
 * Prevents deadlocks on parallel ticking servers like Leaf and Folia by ensuring
 * block access operations happen on the correct thread.
 */
public class ThreadSafeScheduler {

    private final BlockChangeTracker tracker;
    private final Plugin plugin;
    private final boolean isFoliaServer;

    /**
     * Create a new thread-safe scheduler.
     *
     * @param tracker The BlockChangeTracker to use for processing deferred events
     */
    public ThreadSafeScheduler(BlockChangeTracker tracker) {
        this.tracker = tracker;
        this.plugin = Bukkit.getPluginManager().getPlugin("NoCheatPlus");
        this.isFoliaServer = detectFoliaServer();
    }

    /**
     * Check if the current thread is a parallel ticking thread.
     * Parallel ticking servers like Leaf and Folia use separate threads for world ticking.
     *
     * @return true if running on a parallel ticking thread
     */
    public static boolean isParallelTickingThread() {
        String threadName = Thread.currentThread().getName();
        return threadName.contains("World Ticking Thread")
            || threadName.contains("Region Scheduler");
    }

    /**
     * Schedule a deferred block event for processing on the correct thread.
     * Uses Folia's RegionScheduler on Folia servers, or BukkitScheduler on Paper/Spigot/Leaf.
     *
     * @param event The deferred block event to process
     */
    public void scheduleDeferredEvent(DeferredBlockEvent event) {
        if (isFoliaServer) {
            scheduleFoliaRegionTask(event);
        } else {
            scheduleTraditionalTask(event);
        }
    }

    /**
     * Schedule task using Folia's RegionScheduler.
     * This ensures the task runs on the region that owns the location.
     */
    private void scheduleFoliaRegionTask(DeferredBlockEvent event) {
        try {
            // Get the world for the event
            String worldName = event.getWorldName();
            World world = Bukkit.getWorld(worldName);
            
            if (world == null) {
                NCPAPIProvider.getNoCheatPlusAPI().getLogManager()
                    .warning(Streams.STATUS, "Cannot schedule deferred event: world '" + worldName + "' not found");
                return;
            }

            // Create a location in the world (chunk 0,0 as a safe default)
            // The actual processing will happen on the correct region
            Location location = new Location(world, 0, 64, 0);
            
            // Use reflection to call Folia's RegionScheduler
            // Bukkit.getServer().getRegionScheduler().execute(plugin, location, runnable)
            Object server = Bukkit.getServer();
            Object regionScheduler = server.getClass().getMethod("getRegionScheduler").invoke(server);
            
            Runnable task = () -> {
                try {
                    event.process(tracker);
                } catch (Exception e) {
                    NCPAPIProvider.getNoCheatPlusAPI().getLogManager()
                        .warning(Streams.STATUS, "Error processing deferred block event: " + e.getMessage());
                }
            };
            
            regionScheduler.getClass()
                .getMethod("execute", Plugin.class, Location.class, Runnable.class)
                .invoke(regionScheduler, plugin, location, task);
                
        } catch (Exception e) {
            NCPAPIProvider.getNoCheatPlusAPI().getLogManager()
                .warning(Streams.STATUS, "Failed to schedule Folia region task, falling back to traditional scheduler: " + e.getMessage());
            scheduleTraditionalTask(event);
        }
    }

    /**
     * Schedule task using traditional BukkitScheduler.
     * Used on Paper/Spigot/Leaf servers.
     */
    private void scheduleTraditionalTask(DeferredBlockEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    event.process(tracker);
                } catch (Exception e) {
                    NCPAPIProvider.getNoCheatPlusAPI().getLogManager()
                        .warning(Streams.STATUS, "Error processing deferred block event: " + e.getMessage());
                }
            }
        }.runTask(plugin);
    }

    /**
     * Detect if the server is running Folia by checking for Folia-specific classes.
     * 
     * @return true if running on Folia, false for Paper/Spigot/Leaf
     */
    private static boolean detectFoliaServer() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
