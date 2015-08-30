package objects;

public class OpenTile extends Tile {
	private static final long serialVersionUID = 1L;
	
	public OpenTile(){
		
	}

	public String toString() {
		if (this.getState() == State.OPEN) {
			return " ";
		} else {
			return super.toString();
		}
	}

}
