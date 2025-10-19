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
package fr.neatmonster.nocheatplus.compat.blocks.init.vanilla;

import fr.neatmonster.nocheatplus.compat.blocks.BlockPropertiesSetup;
import fr.neatmonster.nocheatplus.compat.blocks.init.BlockInit;
import fr.neatmonster.nocheatplus.config.WorldConfigProvider;
import fr.neatmonster.nocheatplus.config.ConfPaths;
import fr.neatmonster.nocheatplus.config.ConfigFile;
import fr.neatmonster.nocheatplus.config.ConfigManager;
import fr.neatmonster.nocheatplus.logging.StaticLog;
import fr.neatmonster.nocheatplus.utilities.map.BlockFlags;
import fr.neatmonster.nocheatplus.utilities.map.BlockProperties;

/**
 * Blocks for Minecraft 1.21.
 * 
 * @author Claude for NCP (Based author addition, Claude 3.7 -zim)
 */
public class BlocksMC1_21 implements BlockPropertiesSetup {
    
    public BlocksMC1_21() {
        BlockInit.assertMaterialExists("CRAFTER");
        BlockInit.assertMaterialExists("TRIAL_SPAWNER");
        BlockInit.assertMaterialExists("HEAVY_CORE");
        BlockInit.assertMaterialExists("VAULT");
    }
    
    @Override
    public void setupBlockProperties(WorldConfigProvider<?> worldConfigProvider) {
        // Initial 1.21.0-1.21.3 additions
        BlockProperties.setBlockProps("CRAFTER", new BlockProperties.BlockProps(BlockProperties.woodPickaxe, 1.5f));
        BlockFlags.setBlockFlags("CRAFTER", BlockFlags.FULLY_SOLID_BOUNDS);
        
        BlockProperties.setBlockProps("TRIAL_SPAWNER", new BlockProperties.BlockProps(BlockProperties.noTool, 50f));
        BlockFlags.setBlockFlags("TRIAL_SPAWNER", BlockFlags.FULLY_SOLID_BOUNDS);
        
        BlockProperties.setBlockProps("HEAVY_CORE", new BlockProperties.BlockProps(BlockProperties.woodPickaxe, 10f));
        BlockFlags.setBlockFlags("HEAVY_CORE", BlockFlags.SOLID_GROUND);
        
        BlockProperties.setBlockProps("VAULT", new BlockProperties.BlockProps(BlockProperties.woodPickaxe, 10f));
        BlockFlags.setBlockFlags("VAULT", BlockFlags.FULLY_SOLID_BOUNDS);
        
        ConfigFile config = ConfigManager.getConfigFile();
        if (config.getBoolean(ConfPaths.BLOCKBREAK_DEBUG, config.getBoolean(ConfPaths.CHECKS_DEBUG, false)))
            StaticLog.logInfo("Added block-info for Minecraft 1.21 base blocks.");
    }
} 