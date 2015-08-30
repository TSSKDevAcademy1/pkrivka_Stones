import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import objects.PlayerTime;

public class DatabaseSettings {
	public static final String URL = "jdbc:mysql://localhost/stones";
	public static final String USER = "root";
	public static final String PASSWORD = "admin";

	public static final String QUERY_CREATE_BEST_TIMES = "CREATE TABLE player_time (name VARCHAR(128) NOT NULL, best_time INT NOT NULL)";
	public static final String QUERY_ADD_BEST_TIME = "INSERT INTO player_time (name, best_time) VALUES (?, ?)";
	public static final String QUERY_SELECT_BEST_TIMES = "SELECT name, best_time FROM player_time ORDER BY best_time";

	private DatabaseSettings() {
	}

	/**
	 * Load Hall of Fame from database and save it to the list.
	 * 
	 * @return list with player times
	 */
	public static ListOfBestTimes loadHallOfFame() {
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stm = connection.createStatement();
				ResultSet rs = stm.executeQuery(QUERY_SELECT_BEST_TIMES)) {
			ListOfBestTimes list = new ListOfBestTimes();
			while (rs.next()) {
				list.addPlayerTime(new PlayerTime(rs.getString(1), rs.getInt(2)));
			}
			return list;
		} catch (SQLException e) {
			System.err.println("Error occured while loading Hall Of Fame: ");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * In case of winning game, add player time to the database.
	 * 
	 * @param name
	 *            player name
	 * @param time
	 *            player time
	 */
	public static void addTimeToHallOfFame(String name, long time) {
		try {
			Connection connection = DriverManager.getConnection(DatabaseSettings.URL, DatabaseSettings.USER,
					DatabaseSettings.PASSWORD);
			try (Statement stm = connection.createStatement();
					PreparedStatement pstm = connection.prepareStatement(DatabaseSettings.QUERY_ADD_BEST_TIME)) {
				stm.executeUpdate(DatabaseSettings.QUERY_CREATE_BEST_TIMES);
				insertToDatabase(name, time, pstm);
			} catch (Exception e) {
				try (PreparedStatement pstm = connection.prepareStatement(DatabaseSettings.QUERY_ADD_BEST_TIME)) {
					insertToDatabase(name, time, pstm);
				}
			}
		} catch (Exception e) {
			System.out.println("Error occured while saving player time: ");
			e.printStackTrace();
		}
	}

	/**
	 * Insert player parameters to the database.
	 * 
	 * @param name
	 *            player name
	 * @param time
	 *            player time
	 * @param pstm
	 *            prepared statement
	 * @throws SQLException
	 */
	private static void insertToDatabase(String name, long time, PreparedStatement pstm) throws SQLException {
		pstm.setString(1, name);
		pstm.setLong(2, time);
		pstm.execute();
	}

}
