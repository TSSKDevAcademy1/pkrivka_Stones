package objects;
import java.io.Serializable;

public class Tile implements Serializable{
	private static final long serialVersionUID = 1L;

	public enum State {
		NUMBER, OPEN
	}

	private State state;

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String toString() {
		if (this.getState() == State.OPEN) {
			return " ";
		}
		return null;
	}

}
