package meteor;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
	private HashMap<String, Inventory> drops = new HashMap<>();

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (command.getName().equals("meteor")) {
				if (args.length == 0) {
					player.sendMessage("Please specify a name.");
				} else if (!drops.containsKey(args[0])) {
					if (args[0].equals("list")) {
						player.sendMessage("Meteor inventories:");
						for (String s : drops.keySet()) {
							player.sendMessage(" - " + s);
						}
					} else {
						player.sendMessage("Unknown name. Use /meteor list to list inventories");
					}
				} else {
					Inventory common = drops.get(args[0]);
					Location meteorLoc = player.getLocation();
					meteorLoc.setY(100);
					World w = player.getWorld();
					BukkitRunnable run = new BukkitRunnable() {
						private Location loc = meteorLoc;

						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							loc.add(new Vector(0, -0.4, 0));
							w.playEffect(new Location(w, loc.getX(), loc.getY() + 8, loc.getZ()), Effect.EXPLOSION_HUGE,
									3);

							w.playSound(new Location(w, loc.getX(), loc.getY() + 8, loc.getZ()),
									Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
							if (w.getBlockAt(loc).getType() != Material.AIR) {
								loc.setY(loc.getY() + 1);
								w.getBlockAt(loc).setType(Material.CHEST);
								Chest chest = (Chest) w.getBlockAt(loc).getState();

								for (int i = 0; i < common.getSize(); i++) {
									if (common.getItem(i) != null) {
										chest.getInventory().setItem(i, common.getItem(i));
									}
								}

								this.cancel();
							}
						}

					};
					run.runTaskTimer(this, 0, 1);
				}
			} else if (command.getName().equals("meteordrops")) {
				if (args.length == 0) {
					player.sendMessage("Please specify a name.");
				} else {
					if (args[0].equals("list")) {
						player.sendMessage("Can't call an inventory that!");
						return false;
					}
					if (!drops.containsKey(args[0])) {
						drops.put(args[0], Bukkit.createInventory(null, 27, "Common"));
						player.sendMessage("Creating a new inventory with name " + args[0]);
					}
					player.openInventory(drops.get(args[0]));

				}

			}
		}
		return true;

	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();

		Bukkit.getPluginManager().registerEvents(this, this);
		ConsoleCommandSender log = getServer().getConsoleSender();
		log.sendMessage(ChatColor.RED + "Meteors by gamesterrex.");
		log.sendMessage(ChatColor.RED + "Contact me at <robertchen@live.com>");
	}

}
