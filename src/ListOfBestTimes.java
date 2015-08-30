import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import objects.PlayerTime;

public class ListOfBestTimes {
	private List<PlayerTime> playerTimes = new ArrayList<PlayerTime>();

	public Iterator<PlayerTime> iterator() {
		return playerTimes.iterator();
	}

	/**
	 * Add player time to the list.
	 * 
	 * @param playertime
	 *            object of the list.
	 */
	public void addPlayerTime(PlayerTime playertime) {
		playerTimes.add(playertime);
	}

	/**
	 * Get player time from the list.
	 * 
	 * @param index
	 *            player time in the list.
	 * @return return object player time
	 */
	public PlayerTime getPlayerTime(int index) {
		return playerTimes.get(index);
	}

	/**
	 * Get size of the list.
	 * 
	 * @return return size of the list.
	 */
	public int getSize() {
		return playerTimes.size();
	}

}
