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
package fr.neatmonster.nocheatplus.compat.bukkit.model;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

import fr.neatmonster.nocheatplus.utilities.map.BlockCache;

public class BukkitHopper implements BukkitShapeModel {

    @Override
    public double[] getShape(BlockCache blockCache, World world, int x, int y, int z) {
        final Block block = world.getBlockAt(x, y, z);
        final BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional) {
            Directional b = (Directional) blockData;
            BlockFace face = b.getFacing();
            // Minecraft 1.21+ hopper collision shape (hollow bowl, not solid, walkable underneath):
            // - Top rim: 4 walls (2 pixels thick), Y=10-16 (0.625-1.0) - hollow center
            // - Middle funnel: 4-12 on X/Z (0.25-0.75), Y=4-10 (0.25-0.625)
            // - Spout: 6-10 (0.375-0.625) wide, extends in facing direction
            //   - Side facing (N/S/E/W): spout at Y=4-8 (0.25-0.5), bottom Y=0-4 is OPEN
            //   - Down facing: spout at Y=0-4 (0.0-0.25)
            // Players can walk UNDER side-facing hoppers!
            switch (face) {
            case NORTH:
                return new double[] {
                        // Top rim - 4 walls forming hollow bowl (players can walk inside)
                        0.0, 0.625, 0.0, 1.0, 1.0, 0.125,    // North wall
                        0.0, 0.625, 0.875, 1.0, 1.0, 1.0,    // South wall
                        0.0, 0.625, 0.125, 0.125, 1.0, 0.875, // West wall
                        0.875, 0.625, 0.125, 1.0, 1.0, 0.875, // East wall
                        // Middle funnel (4-12 on X/Z, Y=4-10)
                        0.25, 0.25, 0.25, 0.75, 0.625, 0.75,
                        // Spout facing north at Y=4-8 (0.25-0.5) - bottom is OPEN for walking
                        0.375, 0.25, 0.0, 0.625, 0.5, 0.25,
                        };
            case SOUTH:
                return new double[] {
                        // Top rim - 4 walls forming hollow bowl
                        0.0, 0.625, 0.0, 1.0, 1.0, 0.125,    // North wall
                        0.0, 0.625, 0.875, 1.0, 1.0, 1.0,    // South wall
                        0.0, 0.625, 0.125, 0.125, 1.0, 0.875, // West wall
                        0.875, 0.625, 0.125, 1.0, 1.0, 0.875, // East wall
                        // Middle funnel (4-12 on X/Z, Y=4-10)
                        0.25, 0.25, 0.25, 0.75, 0.625, 0.75,
                        // Spout facing south at Y=4-8 (0.25-0.5) - bottom is OPEN for walking
                        0.375, 0.25, 0.75, 0.625, 0.5, 1.0,
                        };
            case WEST:
                return new double[] {
                        // Top rim - 4 walls forming hollow bowl
                        0.0, 0.625, 0.0, 1.0, 1.0, 0.125,    // North wall
                        0.0, 0.625, 0.875, 1.0, 1.0, 1.0,    // South wall
                        0.0, 0.625, 0.125, 0.125, 1.0, 0.875, // West wall
                        0.875, 0.625, 0.125, 1.0, 1.0, 0.875, // East wall
                        // Middle funnel (4-12 on X/Z, Y=4-10)
                        0.25, 0.25, 0.25, 0.75, 0.625, 0.75,
                        // Spout facing west at Y=4-8 (0.25-0.5) - bottom is OPEN for walking
                        0.0, 0.25, 0.375, 0.25, 0.5, 0.625,
                        };
            case EAST:
                return new double[] {
                        // Top rim - 4 walls forming hollow bowl
                        0.0, 0.625, 0.0, 1.0, 1.0, 0.125,    // North wall
                        0.0, 0.625, 0.875, 1.0, 1.0, 1.0,    // South wall
                        0.0, 0.625, 0.125, 0.125, 1.0, 0.875, // West wall
                        0.875, 0.625, 0.125, 1.0, 1.0, 0.875, // East wall
                        // Middle funnel (4-12 on X/Z, Y=4-10)
                        0.25, 0.25, 0.25, 0.75, 0.625, 0.75,
                        // Spout facing east at Y=4-8 (0.25-0.5) - bottom is OPEN for walking
                        0.75, 0.25, 0.375, 1.0, 0.5, 0.625,
                        };
            case DOWN:
                return new double[] {
                        // Top rim - 4 walls forming hollow bowl
                        0.0, 0.625, 0.0, 1.0, 1.0, 0.125,    // North wall
                        0.0, 0.625, 0.875, 1.0, 1.0, 1.0,    // South wall
                        0.0, 0.625, 0.125, 0.125, 1.0, 0.875, // West wall
                        0.875, 0.625, 0.125, 1.0, 1.0, 0.875, // East wall
                        // Middle funnel (4-12 on X/Z, Y=4-10)
                        0.25, 0.25, 0.25, 0.75, 0.625, 0.75,
                        // Spout facing down at Y=0-4 (0.0-0.25) - this IS the bottom
                        0.375, 0.0, 0.375, 0.625, 0.25, 0.625,
                        };
            default:
                break;
            }
        }
        return new double[] {0.0, 0.0, 0.0, 1.0, 1.0, 1.0};
    }

    @Override
    public int getFakeData(BlockCache blockCache, World world, int x, int y, int z) {
        return 0;
    }

}
