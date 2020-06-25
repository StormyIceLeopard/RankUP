package net.blizzardcraft.RankUP;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.huskehhh.mysql.mysql.MySQL;

public class RankUPsqlManager {
	private MySQL db;
	private final RankUP main;
	public RankUPsqlManager(RankUP h) {
		this.main = h;
	}
	
	//Setup DB connection info and create table if not existing
	public void setupDB() throws SQLException, ClassNotFoundException {
		final String mySQLAddress = RankUP.instance.getConfig().getString("MySQL");
		final String mySQLPort = RankUP.instance.getConfig().getString("Port");
		final String mySQLDBName = RankUP.instance.getConfig().getString("DataBaseName");
		final String mySQLUsername = RankUP.instance.getConfig().getString("Username");
		final String mySQLPassword = RankUP.instance.getConfig().getString("Password");
		final Boolean useSSL = RankUP.instance.getConfig().getBoolean("useSSL");
		final Boolean verifyServerCertificate = RankUP.instance.getConfig().getBoolean("verifyServerCertificate");

		this.db = new MySQL(mySQLAddress, mySQLPort, mySQLDBName, 
				mySQLUsername, mySQLPassword, useSSL, verifyServerCertificate);
		this.db.openConnection();
		Statement statement = this.db.getConnection().createStatement();
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS RankUP (id INT NOT NULL AUTO_INCREMENT, "
				+ "username VARCHAR(16) NOT NULL, points INT NOT NULL, last DATETIME NOT NULL, "
				+ "PRIMARY KEY (id))");
		statement.close();
	}
	//Close connection to SQL Database.
	public void closeDB() throws SQLException {
		this.db.closeConnection();
	}

	//Add points to player
	public boolean addPoints(String playername, int points) throws SQLException, ClassNotFoundException {
		if (!this.db.checkConnection()) // Check if a connection is open
			this.db.openConnection(); // Open connection if not already open
		Statement statement = this.db.getConnection().createStatement();
		
		// Check if user exist
		//ResultSet rs = statement.executeQuery("SELECT * FROM " + mySQLDBName + " WHERE `username` = " + playername);
		ResultSet rs = statement.executeQuery("SELECT * FROM `RankUP` WHERE `username` = '" + playername + "'");
		if (!rs.next()) {
			this.main.log("User not found. Adding new user to db.", Level.INFO);
			// INSERT INTO `RankUP`(`id`, `username`, `points`, `last`) VALUES ([value-1],[value-2],[value-3],[value-4])
			statement.executeUpdate("INSERT INTO `RankUP` (`username`, `points`) VALUES ('" + playername +"', 100)");
			statement.close();
			return false; // Exit Adding points to player function
		}
		// Player exists so add points by updating
		// Retreive and store current points
		//int currentPoints = statement.executeQuery(sql);
		points = points + rs.getInt("points");
		//statement.executeUpdate("UPDATE " + mySQLDBName + " SET `points` = " + points + " WHERE `username` = " + playername);
		statement.executeUpdate("UPDATE `RankUP` SET `points` = " + points + " WHERE `username` = '" + playername + "'");
		statement.close();
		return true;
	}
	
	//Retrieve Player's current points.
	public int getPoints(String playername) throws SQLException, ClassNotFoundException {
		if (!this.db.checkConnection()) // Check if a connection is open
			this.db.openConnection(); // Open connection if not already open
		Statement statement = this.db.getConnection().createStatement();
		
		//ResultSet rs = statement.executeQuery("SELECT * FROM " + mySQLDBName + " WHERE `username` = " + playername);
		ResultSet rs = statement.executeQuery("SELECT `points` FROM `RankUP` WHERE `username` = '" + playername + "'");
		if (!rs.next()) {
			this.main.log("User not found.", Level.INFO);
			return -1; // Exit getPoints function
		}
		int points = rs.getInt("points");
		statement.close();
		return points;
	}
	
	//Remove Player's points by amount
	public boolean removePoints(String playername, int points) throws ClassNotFoundException, SQLException {
		int currentPoints = getPoints(playername);
		if (currentPoints == -1) {
			return false;
		}
		int removePoints = currentPoints - points;
		if (removePoints >= 0) {
			if (!this.db.checkConnection()) // Check if a connection is open
				this.db.openConnection(); // Open connection if not already open
			Statement statement = this.db.getConnection().createStatement();
			statement.executeUpdate("UPDATE `RankUP` SET `points`= "+ removePoints +" WHERE `username` = '" + playername + "'");
			statement.close();
			return true;
		}
		else {
			return false;
		}
	}


}