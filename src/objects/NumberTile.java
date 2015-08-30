package objects;

public class NumberTile extends Tile{
	private static final long serialVersionUID = 1L;
	private final int value;

	public NumberTile(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		if (this.getState() == State.NUMBER) {
			return value+"";
		} else {
			return super.toString();
		}
	}
}