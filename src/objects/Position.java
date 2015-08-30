package objects;
import java.io.Serializable;

public class Position implements Serializable{
	private static final long serialVersionUID = 1L;
	private final int row;
	private final int column;

	public Position(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

}
