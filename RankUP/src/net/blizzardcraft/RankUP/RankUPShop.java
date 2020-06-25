package net.blizzardcraft.RankUP;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import xyz.derkades.derkutils.*;
import xyz.derkades.derkutils.bukkit.ItemBuilder;
import xyz.derkades.derkutils.bukkit.menu.IconMenu;
import xyz.derkades.derkutils.bukkit.menu.OptionClickEvent;

public class RankUPShop extends IconMenu {

	FileConfiguration config = RankUP.instance.config;
	public RankUPShop(Plugin plugin, String name, int size, Player player) {
		super(plugin, name, size, player);
		name = config.getString("ShopName");
		size = config.getInt("ShopSize");
		this.setSize(size);
		this.setName(name);
		addItems(player);
	}
	public int PlayerPointsHead;

	private void addItems(Player player) {
		for (final String key : config.getConfigurationSection("Products").getKeys(false)) {
			final ConfigurationSection section = config.getConfigurationSection("Products." + key);

			int slot = Integer.valueOf(key);
			String materialString = section.getString("Item"); // Item in config
			if (materialString.startsWith("head:")) {
				if (section.getBoolean("enabled")) {
					PlayerPointsHead = slot;
					int playerpoints = -1;
					for (int i = 0; i <= 3; i++) {
					try {
						playerpoints = RankUP.instance.mysql.getPoints(player.getName());
						break;
					} catch (SQLException e) {
						RankUP.instance.log("Could not close connection to database. (addItems)", Level.WARNING);
						RankUP.instance.log("Error:"+e, Level.WARNING);
					} catch (ClassNotFoundException e) {
						RankUP.instance.log("Could not find database driver. (addItems)", Level.WARNING);
						RankUP.instance.log("Error:"+e, Level.WARNING);
					}
					}
					List<String> lore = new ArrayList<>(section.getStringList("lore")); // lore in config
					lore = section.getStringList("lore");
					lore = ListUtils.replaceInStringList(lore, new Object[] {"{playerpoints}"}, new Object[] {playerpoints});
					lore = ListUtils.replaceInStringList(lore, new Object[] {"{player}"}, new Object[] {player.getName()});

					String owner = materialString.split(":")[1];
					if (owner.equals("auto")) {
						ItemStack newItem = new ItemBuilder(player.getName()).coloredLore(lore).amount(1).create();
						items.put(slot, newItem);
					}
					else {
						ItemStack newItem = new ItemBuilder(owner).coloredLore(lore).amount(1).create();
						items.put(slot, newItem);
					}
					// Return statement to start back at the begining of for loop
				}
			}
			else {
				Material material = Material.valueOf(materialString);
				String name = section.getString("Name"); // Name in config
				String cost = section.getString("Cost"); // Cost in config
				List<String> lore = new ArrayList<>(section.getStringList("lore")); // lore in config
				lore = section.getStringList("lore");
				lore = ListUtils.replaceInStringList(lore, new Object[] {"{cost}"}, new Object[] {cost});
				ItemStack newItem = new ItemBuilder(material).coloredName(name).coloredLore(lore).amount(1).create();
				items.put(slot, newItem);
			}



		}
	}



	public boolean onOptionClick(OptionClickEvent event) {
		Player player = event.getPlayer();
		int slot = event.getPosition();
		int price = config.getInt("Products." + slot + ".Cost");
		int remainingBalance = transaction(player, price);
		if (price < 0 || slot == PlayerPointsHead) {
			return false; // Do nothing
		}
		if (remainingBalance < 0) {
			remainingBalance = Math.abs(remainingBalance);
			List<String> messageList = new ArrayList<>(config.getStringList("LackPoints.Message")); // lore in config
			messageList = ListUtils.replaceInStringList(messageList, new Object[] {"{neededpoints}"}, new Object[] {remainingBalance});
			for (String message : messageList) {
				player.sendMessage(message);
			}
			return true;
		}
		// Remove points from Player in Database
		// If successful removal of points
		// Execute commands for purchased item
		if (removePoints(player, price)) {
			List<String> commandList = config.getStringList("Products." + slot + ".Commands");
			for (String command : commandList) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
			}
			List<String> messageList = new ArrayList<>(config.getStringList("Products." + slot + ".PurchaseMessage")); // lore in config
			messageList = ListUtils.replaceInStringList(messageList, new Object[] {"{remainingBalance}"}, new Object[] {remainingBalance});
			for (String message : messageList) {
				player.sendMessage(message);
			}
			//return false; // Leave the menu open when item is clicked
			return true; // Close the menu
		}
		player.sendMessage("A Connection Error has occured with your transaction. Try again later. If the issue still persists contact staff.");
		return true;
	}

	public int transaction(Player player, int cost) {
		int playerpoints = -1;
		for (int i = 0; i <= 3; i++) {
		try {
			playerpoints = RankUP.instance.mysql.getPoints(player.getName());
			break;
		} catch (SQLException e) {
			RankUP.instance.log("Could not close connection to database. (transaction)", Level.WARNING);
			RankUP.instance.log("Error:"+e, Level.WARNING);
		} catch (ClassNotFoundException e) {
			RankUP.instance.log("Could not find database driver. (transaction)", Level.WARNING);
			RankUP.instance.log("Error:"+e, Level.WARNING);
		}
		}
		return playerpoints - cost; // Return remaining points after product cost
	}
	
	public boolean removePoints(Player playername, int points) {
		for (int i = 0; i <= 3; i++) {
		try {
			RankUP.instance.mysql.removePoints(playername.getName(), points);
			return true;
		} catch (ClassNotFoundException e) {
			RankUP.instance.log("Could not close connection to database. (removePoints Shop)", Level.WARNING);
			RankUP.instance.log("Error:"+e, Level.WARNING);
		} catch (SQLException e) {
			RankUP.instance.log("Could not find database driver. (removePoints Shop)", Level.WARNING);
			RankUP.instance.log("Error:"+e, Level.WARNING);
		}
		}
		return false;
	}
}
