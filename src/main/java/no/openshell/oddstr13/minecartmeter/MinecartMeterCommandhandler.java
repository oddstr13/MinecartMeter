package no.openshell.oddstr13.minecartmeter;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handler for the /pos sample command.
 *
 * @author SpaceManiac
 */
public class MinecartMeterCommandhandler implements CommandExecutor {
	private final MinecartMeter plugin;

	public MinecartMeterCommandhandler(MinecartMeter plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (plugin.config.getBoolean("debug", false)) {
			System.out.println("[DEBUG]: command minecartmeter executed");
		}

		boolean isconsolle = false;
		boolean isop = false;
		boolean isplayer = false;
		boolean debug = plugin.config.getBoolean("debug", false);
		Player player;

		if (sender instanceof Player) {
			player = (Player) sender;
			isop = player.isOp();
			isplayer = true;
		} else {
			isconsolle = true;
		}

		if (split.length == 1) {
			if (isconsolle || isop) {
				if (split[0].equalsIgnoreCase("reload")) {
					if (debug) {
						System.out.println("[DEBUG]: reloading config");
					}
					plugin.refreshConfigFile();
					if (debug) {
						System.out.println("[DEBUG]: config reloaded");
					}
					if (isplayer) {
						player = (Player) sender;
						player.sendMessage("MinecartMeter config reloaded.");
					}
					return true;
				}
			}
		}

		if (!(isplayer)) {
			return false;
		}
		player = (Player) sender;
		player.sendMessage("/minecartmeter");
		return true;
	}
}
