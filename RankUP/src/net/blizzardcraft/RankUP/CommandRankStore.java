package net.blizzardcraft.RankUP;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.blizzardcraft.RankUP.RankUP;

public class CommandRankStore implements CommandExecutor {
	private final RankUP main;

	public CommandRankStore(RankUP h) {
		this.main = h;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 0) {
				new RankUPShop(main, "Shop Name not set in config!", 9, player).open();
			} 
			// Add points to player
			else if (args[0].equals("add") && (player.hasPermission("rankup.*") || player.hasPermission("rankup.add"))) {
				if (args.length == 1) {
					player.sendMessage("Correct Command Usage: /rankstore add [playername] <points>");
				} 
				// Add Points to self
				else if (args.length == 2) {
					int points = 0;
					try {
						points = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.sendMessage("'" + args[1] + "' is not a valid number. Please use a whole number.");
					}
					for (int i = 0; i <= 3; i++) {
					try {
						this.main.mysql.addPoints(player.getName(), points);
						player.sendMessage("Successfully added " + points + " to " + player.getName() + ".");
						player.sendMessage(player.getName() + " now has " + this.main.mysql.getPoints(player.getName()) + " points.");
						break;
					} catch (ClassNotFoundException e) {
						RankUP.instance.log("Could not find database driver. (Command 'add' addPoints)", Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					} catch (SQLException e) {
						RankUP.instance.log(
								"Could not connect to database during getCommands. (Command 'add' addPoints)",
								Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					}
				}
				} 
				// Add Points to defined playername
				else if (args.length == 3 && (player.hasPermission("rankup.*") || player.hasPermission("rankup.add.*")
						|| player.hasPermission("rankup.add.others"))) {
					int points = 0;
					try {
						points = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						player.sendMessage("'" + args[2] + "' is not a valid number. Please use a whole number.");
					}
					for (int i = 0; i <= 3; i++) {
					try {
						this.main.mysql.addPoints(args[1], points);
						player.sendMessage("Successfully added " + points + " to " + args[1] + ".");
						player.sendMessage(args[1] + " now has " + this.main.mysql.getPoints(args[1]) + " points.");
						break;
					} catch (ClassNotFoundException e) {
						RankUP.instance.log("Could not find database driver. (Command 'add' addPoints)", Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					} catch (SQLException e) {
						RankUP.instance.log(
								"Could not connect to database during getCommands. (Command 'add' addPoints)",
								Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					}
				}
				} else {
					player.sendMessage("You do not have permission to use that command.");
				}
			} 
			// Check player's current points
			else if (args[0].equals("check") && (player.hasPermission("rankup.*")
					|| player.hasPermission("rankup.check.*") || player.hasPermission("rankup.check"))) {
				// Check self's current points
				if (args.length <= 1) {
					for (int i = 0; i <= 3; i++) {
					try {
						int points = this.main.mysql.getPoints(player.getName());
						if (points == -1) {
							player.sendMessage(player.getName() + " was not found in database.");
						}
						else {
							player.sendMessage(player.getName() + " has " + points + " points.");
						}
						break;
					} catch (ClassNotFoundException e) {
						RankUP.instance.log("Could not find database driver. (Command 'check' addPoints)",
								Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					} catch (SQLException e) {
						RankUP.instance.log(
								"Could not connect to database during getCommands. (Command 'check' addPoints)",
								Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					}
				}
				} 
				// Check defined playername's current points
				else if (args.length == 2 && (player.hasPermission("rankup.*")
						|| player.hasPermission("rankup.check.*") || player.hasPermission("rankup.check.others"))) {
					String playername = args[1];
					for (int i = 0; i <= 3; i++) {
					try {
						int points = this.main.mysql.getPoints(playername);
						if (points == -1) {
							player.sendMessage(playername + " was not found in database.");
						}
						else {
							player.sendMessage(playername + " has " + points + " points.");
						}
						break;
					} catch (ClassNotFoundException e) {
						RankUP.instance.log("Could not find database driver. (Command 'check' addPoints)",
								Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					} catch (SQLException e) {
						RankUP.instance.log(
								"Could not connect to database during getCommands. (Command 'check' addPoints)",
								Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					}
				}
				} else {
					player.sendMessage("You do not have permission to use that command.");
				}
			} 
			// Remove points from player
			else if (args[0].equals("remove") && (player.hasPermission("rankup.*")
					|| player.hasPermission("rankup.remove"))) {
				if (args.length == 1) {
					player.sendMessage("Correct Command Usage: /rankstore remove [playername] <points>");
				} 
				// Remove points from self
				else if (args.length == 2) {
					int points = 0;
					boolean result = false;
					try {
						points = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.sendMessage("'" + args[1] + "' is not a valid number. Please use a whole number.");
					}
					for (int i = 0; i <= 3; i++) {
					try {
						result = this.main.mysql.removePoints(player.getName(), points);
						if (!result) {
							player.sendMessage("Cannot remove " + points + " points from " + player.getName()
									+ ". This would result in a negative balance.");
						}
						else {
							player.sendMessage("Successfully removed " + points + " from " + player.getName() + ".");
							player.sendMessage(player.getName() + " now has " + this.main.mysql.getPoints(player.getName()) + " points.");
						}
						break;
					} catch (ClassNotFoundException e) {
						RankUP.instance.log("Could not find database driver. (Command 'remove' addPoints)",
								Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					} catch (SQLException e) {
						RankUP.instance.log(
								"Could not connect to database during getCommands. (Command 'remove' addPoints)",
								Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					}
					}
				} 
				// Remove points from defined playername
				else if (args.length == 3 && (player.hasPermission("rankup.*")
						|| player.hasPermission("rankup.remove.*") || player.hasPermission("rankup.remove.others"))) {
					int points = 0;
					String playername = args[1];
					boolean result = false;
					try {
						points = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						player.sendMessage("'" + args[2] + "' is not a valid number. Please use a whole number.");
					}
					for (int i = 0; i <= 3; i++) {
					try {
						result = this.main.mysql.removePoints(playername, points);
						if (!result) {
							player.sendMessage("Cannot remove " + points + " points from " + playername
									+ ". This would result in a negative balance.");
						}
						else {
							player.sendMessage("Successfully removed " + points + " from " + playername + ".");
							player.sendMessage(playername + " now has " + this.main.mysql.getPoints(playername) + " points.");
						}
						break;
					} catch (ClassNotFoundException e) {
						RankUP.instance.log("Could not find database driver. (Command 'remove' addPoints)",
								Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					} catch (SQLException e) {
						RankUP.instance.log(
								"Could not connect to database during getCommands. (Command 'remove' addPoints)",
								Level.WARNING);
						RankUP.instance.log("Error:" + e, Level.WARNING);
					}
					}
				} else {
					player.sendMessage("You do not have permission to use that command.");
				}
			}
		}
		else {
			if (args[0].equals("add") && args.length == 3) {
//				if (args.length == 3 && (player.hasPermission("rankup.*") || player.hasPermission("rankup.add.*")
//				|| player.hasPermission("rankup.add.others"))) {
			int points = 0;
			try {
				points = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				//player.sendMessage("'" + args[2] + "' is not a valid number. Please use a whole number.");
			}
			for (int i = 0; i <= 3; i++) {
			try {
				this.main.mysql.addPoints(args[1], points);
				//player.sendMessage("Successfully added " + points + " to " + args[1] + ".");
				//player.sendMessage(args[1] + " now has " + this.main.mysql.getPoints(args[1]) + " points.");
				break;
			} catch (ClassNotFoundException e) {
				RankUP.instance.log("Could not find database driver. (Command 'add' addPoints)", Level.WARNING);
				RankUP.instance.log("Error:" + e, Level.WARNING);
			} catch (SQLException e) {
				RankUP.instance.log(
						"Could not connect to database during getCommands. (Command 'add' addPoints)",
						Level.WARNING);
				RankUP.instance.log("Error:" + e, Level.WARNING);
			}
		}
//		} else {
//			player.sendMessage("You do not have permission to use that command.");
//		}
//	} 
			}
		}
		
		return true;
	}
}