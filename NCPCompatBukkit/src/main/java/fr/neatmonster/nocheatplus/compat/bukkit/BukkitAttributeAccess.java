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
package fr.neatmonster.nocheatplus.compat.bukkit;

import java.util.UUID;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.compat.AttribUtil;
import fr.neatmonster.nocheatplus.components.modifier.IAttributeAccess;
import fr.neatmonster.nocheatplus.utilities.ReflectionUtil;

public class BukkitAttributeAccess implements IAttributeAccess {

    private static final Attribute ATTRIBUTE_MOVEMENT_SPEED;
    static {
        Attribute attr = null;
        try {
            attr = Attribute.valueOf("MOVEMENT_SPEED");
        } catch (IllegalArgumentException e) {
            try {
                attr = Attribute.valueOf("GENERIC_MOVEMENT_SPEED");
            } catch (IllegalArgumentException e2) {}
        }
        ATTRIBUTE_MOVEMENT_SPEED = attr;
    }

    public BukkitAttributeAccess() {
        if (ReflectionUtil.getClass("org.bukkit.attribute.AttributeInstance") == null) {
            throw new RuntimeException("Service not available.");
        }
    }

    private int operationToInt(final Operation operation) {
        return operation.ordinal();
    }

    /**
     * The first modifier with the given id that can be found, or null if none
     * is found.
     * 
     * @param attrInst
     * @param id
     * @return
     */
    private AttributeModifier getModifier(final AttributeInstance attrInst, final UUID id) {
        for (final AttributeModifier mod : attrInst.getModifiers()) {
            if (id.equals(mod.getUniqueId())) {
                return mod;
            }
        }
        return null;
    }

    private double getMultiplier(final AttributeModifier mod) {
        return AttribUtil.getMultiplier(operationToInt(mod.getOperation()), mod.getAmount());
    }

    @Override
    public double getSpeedAttributeMultiplier(final Player player) {
        final AttributeInstance attrInst = player.getAttribute(ATTRIBUTE_MOVEMENT_SPEED);
        final double val = attrInst.getValue() / attrInst.getBaseValue();
        final AttributeModifier mod = getModifier(attrInst, AttribUtil.ID_SPRINT_BOOST);
        return mod == null ? val : (val / getMultiplier(mod));
    }

    @Override
    public double getSprintAttributeMultiplier(final Player player) {
        final AttributeInstance attrInst = player.getAttribute(ATTRIBUTE_MOVEMENT_SPEED);
        final AttributeModifier mod = getModifier(attrInst, AttribUtil.ID_SPRINT_BOOST);
        return mod == null ? 1.0 : getMultiplier(mod);
    }

}
