package net.blizzardcraft.RankUP;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RankUPListener implements Listener {
	private final RankUP main;
	public RankUPListener(RankUP h) {
		this.main = h;
	}
	final int reach = RankUP.instance.getConfig().getInt("timeonline"); // Time to reach to receive points. 900 seconds aka 15 minutes
	//final int reach = 900;
	//public static int TaskID;
	//public static int time;
	// Map to track player's time
	//Map<UUID, Integer> playercountdown = new HashMap<UUID, Integer>();
	Map<UUID, Integer> taskIDTracker = new HashMap<UUID, Integer>();

	@EventHandler
	void onPlayerJoin(PlayerJoinEvent join) {
		if (!join.getPlayer().hasPlayedBefore()) {
			join.getPlayer().sendMessage("Welcome, " + join.getPlayer().getName() + 
					"! Since this is your first time joining the server, BCN would like to reward you with 100 points to help you get ranks in /rankstore!");
			for (int i = 0; i < 3; i++) {
				try {
					RankUP.instance.mysql.addPoints(join.getPlayer().getName(), 100);
					break;
				} catch (ClassNotFoundException e) {
					RankUP.instance.log("Could not find database driver. (listener first join)", Level.WARNING);
					RankUP.instance.log("Error:"+e, Level.WARNING);
				} catch (SQLException e) {
					RankUP.instance.log("Could not connect to database. (listener first join)", Level.WARNING);
					RankUP.instance.log("Error:"+e, Level.WARNING);
				}
			}
		}
		if (taskIDTracker.get(join.getPlayer().getUniqueId()) != null) {
			taskIDTracker.remove(join.getPlayer().getUniqueId());
			Bukkit.getScheduler().cancelTask(taskIDTracker.get(join.getPlayer().getUniqueId()));
			taskIDTracker.remove(join.getPlayer().getUniqueId());
		}
		//startTimer(join.getPlayer().getUniqueId(), join.getPlayer().getDisplayName(), join.getPlayer());
		//playercountdown.put(join.getPlayer().getUniqueId(), 1);
		//Bukkit.broadcastMessage("Player taskID: " + TaskID);
		int TaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
			@Override
			public void run() {
				if (taskIDTracker.get(join.getPlayer().getUniqueId()) == null) {
					//Bukkit.broadcastMessage("Stopping Task: " + taskIDTracker.get(join.getPlayer().getUniqueId()));
					Bukkit.getScheduler().cancelTask(taskIDTracker.get(join.getPlayer().getUniqueId()));
					taskIDTracker.remove(join.getPlayer().getUniqueId());
				}
				//time = playercountdown.get(join.getPlayer().getUniqueId());
				//time = time - 300;
				//playercountdown.put(join.getPlayer().getUniqueId(), time);
				//Bukkit.broadcastMessage(join.getPlayer().getDisplayName() + " has " + playercountdown.get(join.getPlayer().getUniqueId()) + " seconds left.");
				//if (playercountdown.get(join.getPlayer().getUniqueId()) == 0) {
					//playercountdown.put(join.getPlayer().getUniqueId(), reach);
					//Bukkit.broadcastMessage(join.getPlayer().getDisplayName() + " has reached the online time and should be restarting.");
					//Code to add points to player DisplayName
					//Should be handled from RankUPsqlManager
					for (int i = 0; i <= 3; i++) {
						try {
							RankUP.instance.mysql.addPoints(join.getPlayer().getName(), RankUP.instance.getConfig().getInt("points"));
							break;
						} catch (ClassNotFoundException e) {
							RankUP.instance.log("Could not find database driver. (listener addPoints)", Level.WARNING);
							RankUP.instance.log("Error:"+e, Level.WARNING);
						} catch (SQLException e) {
						RankUP.instance.log("Could not connect to database during getCommands. (listener addPoints)", Level.WARNING);
						RankUP.instance.log("Error:"+e, Level.WARNING);
					}
					}
				//}
			}
		}, 20*reach, 20*reach);
		taskIDTracker.put(join.getPlayer().getUniqueId(), TaskID);

	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent quit) {
		//playercountdown.remove(quit.getPlayer().getUniqueId());
		Bukkit.getScheduler().cancelTask(taskIDTracker.get(quit.getPlayer().getUniqueId()));
		taskIDTracker.remove(quit.getPlayer().getUniqueId());
		if (taskIDTracker.isEmpty() == true) {
			stopAll();
		}
	}

	public void stopAll() {
		Bukkit.getScheduler().cancelTasks(main);
	}

	public void stopTask(int task) {
		Bukkit.getScheduler().cancelTask(task);
	}

	// Subtract Points when player sends them.
}