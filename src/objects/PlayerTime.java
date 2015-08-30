package objects;

public class PlayerTime {

	private final String name;
	private final int time;

	public PlayerTime(String name, int time) {
		this.name = name;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public int getTime() {
		return time;
	}

}
