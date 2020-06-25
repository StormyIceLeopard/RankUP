package net.blizzardcraft.RankUP;

import java.sql.SQLException;
import java.util.logging.Level;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class RankUP extends JavaPlugin{
	private static RankUP plugin;
	public static RankUP getPlugin() {
		return plugin;
	}
	// Variables
	public boolean logDB = true;
	public final boolean debug = true;
	public RankUPsqlManager mysql = new RankUPsqlManager(this);
	public FileConfiguration config = this.getConfig();
	public static RankUP instance;
	

	//Execute when Plugin is first enabled.
	@Override
	public void onEnable() {
		instance = this;
		this.saveDefaultConfig();
		if (RankUP.instance.getConfig().getString("MySQL").equals("change me")) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		try {
			this.mysql.setupDB();
		} catch (SQLException e) {
			this.logDB = false;
			this.log("Could not connect to database.", Level.WARNING);
			this.log("Error:"+e, Level.WARNING);
		} catch (ClassNotFoundException e) {
			this.log("Could not find database driver.", Level.WARNING);
			this.log("Error:"+e, Level.WARNING);
		}

		// Register the command "RankStore" (set an instance of your command class as executor)
		getServer().getPluginManager().registerEvents(new RankUPListener(this), this);
		this.getCommand("RankStore").setExecutor(new CommandRankStore(this));
		this.log("RankUP Plugin has been enabled.", Level.INFO); //Execute to console when Plugin is enabled.
	}

	//Console Log
	public void log(String s, Level l) {
		getLogger().log(l, "[RankUP] " + s);
	}

	//Execute when Plugin is disabled.
	@Override
	public void onDisable() {
		if (RankUP.instance.getConfig().getString("MySQL").equals("change me")) {
			this.log("You need to setup the MySQL Information in the config.yml", Level.WARNING);
			this.log("RankUP Plugin has been disabled.", Level.INFO);
			return;
		}
		try {
			this.mysql.closeDB();
		} catch (SQLException e) {
			this.log("Could not close connection to database.", Level.WARNING);
			this.log("Error:"+e, Level.WARNING);
		}
		this.log("RankUP Plugin has been disabled.", Level.INFO); //Execute to console when Plugin is disabled.
	}
}