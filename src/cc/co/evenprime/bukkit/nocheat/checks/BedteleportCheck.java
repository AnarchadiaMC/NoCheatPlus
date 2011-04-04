package cc.co.evenprime.bukkit.nocheat.checks;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerMoveEvent;

import cc.co.evenprime.bukkit.nocheat.NoCheat;

public class BedteleportCheck extends Check {

	public BedteleportCheck(NoCheat plugin) {
		super(plugin);
		setActive(true);
	}

	public void check(PlayerMoveEvent event) {

		// Should we check at all?
		if(plugin.hasPermission(event.getPlayer(), "nocheat.bedteleport")) 
			return;

		if(event.getFrom().getWorld().getBlockTypeIdAt(event.getFrom()) == Material.BED_BLOCK.getId()) {
			double yRest = event.getFrom().getY() - Math.floor(event.getFrom().getY());
			if(yRest > 0.099 && yRest < 0.101)
				// Don't allow the teleport
				event.setCancelled(true);
		}
	}

	@Override
	public String getName() {
		return "bedteleport";
	}
}
